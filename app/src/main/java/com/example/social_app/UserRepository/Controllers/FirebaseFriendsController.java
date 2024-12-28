package com.example.social_app.UserRepository.Controllers;

import com.example.social_app.UserRepository.Controllers.Callbacks.FriendsCallback.OnFriendsSuggestListener;
import com.example.social_app.UserRepository.Controllers.Callbacks.FriendsCallback.OnLoadFriendsListener;
import com.example.social_app.UserRepository.Models.UserModel;
import com.example.social_app.Utils.Common.NetworkManager;
import com.example.social_app.social_Repo.Adapters.Callbacks.OnActiveFriendsLoadingListener;
import com.example.social_app.social_Repo.Adapters.Callbacks.OnFriendRequestActionListener;
import com.example.social_app.social_Repo.Adapters.Callbacks.OnFriendRequestsLoadedListener;
import com.example.social_app.social_Repo.Adapters.Callbacks.OnSendFriendRequestListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseFriendsController {

  public  static  void loadFriends(String currentUserId, OnLoadFriendsListener onLoadFriendsListener){
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

// Assuming `currentUserId` is the logged-in user's ID
        usersRef.child(currentUserId).child("friends").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Map<String, Boolean> friendsMap = task.getResult().getValue(new GenericTypeIndicator<Map<String, Boolean>>() {});

                if (friendsMap != null) {
                    for (String friendId : friendsMap.keySet()) {
                        // Fetch and display each friend's details
                        usersRef.child(friendId).get().addOnSuccessListener(friendSnapshot -> {
                            UserModel friend = friendSnapshot.getValue(UserModel.class);
                            if (friend != null) {
                                // Add friend to your RecyclerView/ListView
                                System.out.println("Friend: " + friend.getUsername());
                            }
                        });
                    }
                } else {
                    System.out.println("No friends found.");
                }
            } else {
                System.err.println("Failed to load friends: " + task.getException().getMessage());
            }
        });

    }
    public static void suggestFriend(UserModel currUser, OnFriendsSuggestListener onFriendsSuggestListener) {
        if (currUser == null || onFriendsSuggestListener == null) {
            onFriendsSuggestListener.OnFriendsSuggestionFailure("Invalid input: current user or listener is null.");
            return;
        }
        if(!NetworkManager.isInternetAvailable()){
            onFriendsSuggestListener.OnFriendsSuggestionFailure("Network error, please try again later.");
            return;
        }

        // Initialize maps to prevent null pointer exceptions
        Map<String, Boolean> friends = currUser.getFriends() != null ? currUser.getFriends() : new HashMap<>();
        Map<String, Boolean> receivedRequests = currUser.getReceivedFriendRequests() != null ? currUser.getReceivedFriendRequests() : new HashMap<>();
        Map<String, Boolean> sentRequests = currUser.getSentFriendRequests() != null ? currUser.getSentFriendRequests() : new HashMap<>();

        // Reference to all users in the database
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Fetch all users from Firebase
        usersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<UserModel> suggestedFriends = new ArrayList<>();

                for (DataSnapshot userSnapshot : task.getResult().getChildren()) {
                    UserModel potentialFriend = userSnapshot.getValue(UserModel.class);

                    if (potentialFriend != null && !potentialFriend.getUserId().equals(currUser.getUserId())) {
                        String potentialFriendId = potentialFriend.getUserId();

                        // Filter out:
                        // - Users who are already friends
                        // - Users who already have sent or received friend requests (either pending or completed)
                        if (!friends.containsKey(potentialFriendId) &&
                                !receivedRequests.containsKey(potentialFriendId) &&
                                !sentRequests.containsKey(potentialFriendId)) {

                            // Only add to suggestions if not already in requests or friends list
                            suggestedFriends.add(potentialFriend);
                        }
                    }
                }

                // Return suggestions through the listener

                    onFriendsSuggestListener.OnFriendSuggestSucces(suggestedFriends);


            } else {
                // Handle database read failure
                onFriendsSuggestListener.OnFriendsSuggestionFailure("Failed to retrieve users: " +
                        (task.getException() != null ? task.getException().getLocalizedMessage() : "Unknown error"));
            }
        }).addOnFailureListener(e -> {
            onFriendsSuggestListener.OnFriendsSuggestionFailure("Error accessing database: " + e.getLocalizedMessage());
        });
    }

    public static void loadFriendRequestsWithDetails(String userId, OnFriendRequestsLoadedListener listener) {
        if (userId == null || userId.isEmpty()) {
            listener.onFailure("Invalid user ID.");
            return;
        }
        if(!NetworkManager.isInternetAvailable()){
            listener.onFailure("Network error, please try again later.");
            return;
        }

        // Firebase database reference
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        // Fetch received friend requests
        DatabaseReference receivedRequestsRef = userRef.child("receivedFriendRequests");
        receivedRequestsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Map<String, Boolean> receivedRequests = new HashMap<>();
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    receivedRequests.put(snapshot.getKey(), snapshot.getValue(Boolean.class));
                }

                if (receivedRequests.isEmpty()) {
                    listener.onFriendRequestsLoaded(new ArrayList<>());
                    return;
                }

                // Fetch details for each user ID in received friend requests
                ArrayList<UserModel> userModels = new ArrayList<>();
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

                for (String requesterId : receivedRequests.keySet()) {
                    usersRef.child(requesterId).get().addOnCompleteListener(userTask -> {
                        if (userTask.isSuccessful() && userTask.getResult() != null) {
                            UserModel userModel = userTask.getResult().getValue(UserModel.class);
                            if (userModel != null) {
                                userModels.add(userModel);
                            }

                            // Check if all users have been processed
                            if (userModels.size() == receivedRequests.size()) {
                                listener.onFriendRequestsLoaded(userModels);
                            }
                        } else {
                            listener.onFailure("Failed to fetch details for user: " + requesterId);
                        }
                    }).addOnFailureListener(e -> {
                        listener.onFailure("Error fetching user details: " + e.getLocalizedMessage());
                    });
                }

            } else {
                // Handle received requests failure
                listener.onFailure("Failed to load received friend requests: "+(task.getException() != null ? task.getException().getLocalizedMessage() : "Unknown error"));
            }
        }).addOnFailureListener(e -> {
            listener.onFailure("Error accessing database: " + e.getLocalizedMessage());
        });
    }





    public static void sendRequest(String senderId, String receiverId, OnSendFriendRequestListener listener) {
        // Reference to Firebase Realtime Databasez

        if(!NetworkManager.isInternetAvailable()){
            listener.onRequestSendFailure("Network error, please try again later.");
            return;
        }
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // References to the specific nodes for received and sent friend requests
        DatabaseReference receiverRef = databaseReference.child("users").child(receiverId).child("receivedFriendRequests");
        DatabaseReference senderRef = databaseReference.child("users").child(senderId).child("sentFriendRequests");

        receiverRef.child(senderId).setValue(false).addOnSuccessListener(unused -> {
            senderRef.child(receiverId).setValue(false).addOnSuccessListener(unused1 -> {
                listener.onRequestSent(senderId,receiverId);
            }).addOnFailureListener(e -> {
                listener.onRequestSendFailure(e.getLocalizedMessage());
            });
        }).addOnFailureListener(e -> {
            listener.onRequestSendFailure(e.getLocalizedMessage());

        });

    }


    public static void loadFriends(String userId, OnActiveFriendsLoadingListener listener) {
        if (userId == null || userId.isEmpty()) {
            listener.onFailure("Invalid user ID.");
            return;
        }

        if(!NetworkManager.isInternetAvailable()){
            listener.onFailure("Network error, please try again later.");
            return;
        }

        // Reference to the current user's friends list in Firebase
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("friends");
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Map<String, Boolean> friendsMap = new HashMap<>();
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    friendsMap.put(snapshot.getKey(), snapshot.getValue(Boolean.class));
                }

                if (friendsMap.isEmpty()) {
                    listener.onFriendsLoaded(new ArrayList<>()); // No friends
                    return;
                }

                // Fetch details for each friend
                ArrayList<UserModel> friendList = new ArrayList<>();
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

                for (String friendId : friendsMap.keySet()) {
                    usersRef.child(friendId).get().addOnCompleteListener(friendTask -> {
                        if (friendTask.isSuccessful() && friendTask.getResult() != null) {
                            UserModel friend = friendTask.getResult().getValue(UserModel.class);
                            if (friend != null) {
                                friendList.add(friend);
                            }

                            // Check if all friends have been processed
                            if (friendList.size() == friendsMap.size()) {
                                listener.onFriendsLoaded(friendList);
                            }
                        } else {
                            listener.onFailure("Failed to load details for friend ID: " + friendId);
                        }
                    }).addOnFailureListener(e -> {
                        listener.onFailure("Error fetching friend details: " + e.getLocalizedMessage());
                    });
                }
            } else {
                listener.onFailure("Failed to load friends list: " +
                        (task.getException() != null ? task.getException().getLocalizedMessage() : "Unknown error"));
            }
        }).addOnFailureListener(e -> {
            listener.onFailure("Error accessing database: " + e.getLocalizedMessage());
        });
    }


    public static void acceptFriendRequest(String receiverId, String senderId, OnFriendRequestActionListener listener) {
        if (receiverId == null || receiverId.isEmpty() || senderId == null || senderId.isEmpty()) {
            listener.onFailure("Invalid sender or receiver ID.");
            return;
        }

        if(!NetworkManager.isInternetAvailable()){
            listener.onFailure("Network error, please try again later.");
            return;
        }

        // Firebase database reference
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        // References for sender and receiver
        DatabaseReference receiverRef = databaseRef.child("users").child(receiverId);
        DatabaseReference senderRef = databaseRef.child("users").child(senderId);

        // Remove the friend request from both users
        receiverRef.child("receivedFriendRequests").child(senderId).removeValue().addOnSuccessListener(unused1 -> {
            senderRef.child("sentFriendRequests").child(receiverId).removeValue().addOnSuccessListener(unused2 -> {

                // Add each other to the friends list
                receiverRef.child("friends").child(senderId).setValue(true).addOnSuccessListener(unused3 -> {
                    senderRef.child("friends").child(receiverId).setValue(true).addOnSuccessListener(unused4 -> {
                        listener.onSuccess("Friend request accepted successfully.");
                    }).addOnFailureListener(e -> {
                        listener.onFailure("Failed to add receiver to sender's friend list: " + e.getLocalizedMessage());
                    });
                }).addOnFailureListener(e -> {
                    listener.onFailure("Failed to add sender to receiver's friend list: " + e.getLocalizedMessage());
                });

            }).addOnFailureListener(e -> {
                listener.onFailure("Failed to remove sent friend request: " + e.getLocalizedMessage());
            });
        }).addOnFailureListener(e -> {
            listener.onFailure("Failed to remove received friend request: " + e.getLocalizedMessage());
        });
    }


    public static void unfriend(String userId, String friendId, OnFriendRequestActionListener listener) {
        if (userId == null || userId.isEmpty() || friendId == null || friendId.isEmpty()) {
            listener.onFailure("Invalid user ID or friend ID.");
            return;
        }
        if(!NetworkManager.isInternetAvailable()){
            listener.onFailure("Network error, please try again later.");
            return;
        }
        // Firebase database reference
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        // References for both users
        DatabaseReference userRef = databaseRef.child("users").child(userId).child("friends").child(friendId);
        DatabaseReference friendRef = databaseRef.child("users").child(friendId).child("friends").child(userId);

        // Remove friend relationship from both sides
        userRef.removeValue().addOnSuccessListener(unused1 -> {
            friendRef.removeValue().addOnSuccessListener(unused2 -> {
                listener.onSuccess("Unfriended successfully.");
            }).addOnFailureListener(e -> {
                listener.onFailure("Failed to remove user from friend's list: " + e.getLocalizedMessage());
            });
        }).addOnFailureListener(e -> {
            listener.onFailure("Failed to remove friend from user's list: " + e.getLocalizedMessage());
        });
    }

}
