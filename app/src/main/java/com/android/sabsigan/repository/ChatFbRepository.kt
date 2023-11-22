package com.android.sabsigan.repository

import android.util.Log
import com.android.sabsigan.data.ChatMessage
import com.android.sabsigan.data.ChatRoom
import com.android.sabsigan.data.User
import com.android.sabsigan.viewModel.ChatViewModel
import com.android.sabsigan.viewModel.MainViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat


class ChatFbRepository(): FirebaseRepository() {

    suspend fun getMessageList(cid: String): ArrayList<ChatMessage> = withContext(Dispatchers.IO) {
        val items = ArrayList<ChatMessage>()

        try {
            val messageRef = db.collection("chatRooms").document(cid).collection("messages")

            messageRef.get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.d("getChatRooms", "${document.id} => ${document.data}")

                        val chatMessage = ChatMessage(
                            cid = document["cid"] as String,
                            uid = document["uid"] as String,
                            id = document["id"] as String,
                            userName = document["userName"] as String,
                            text = document["text"] as String,
                            type = document["type"] as String,
                            created_at = document["created_at"] as String,
                            updated_at = document["updated_at"] as String,
                        )

                        items.add(chatMessage)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("getChatRooms", "Error getting documents: ", exception)
                }


            messageRef.addSnapshotListener { snapshots, e ->
                // 오류 발생 시
                if (e != null) {
                    Log.w("fff", "Listen failed: $e")
                    return@addSnapshotListener
                }

                // 원하지 않는 문서 무시
                if (snapshots!!.metadata.isFromCache) return@addSnapshotListener

                var cnt = 0
                for (doc in snapshots.documentChanges) {
                    Log.d("firebase", "${doc.document.id} => ${doc.document.data}")

                    val chatMessage = ChatMessage(
                        cid = doc.document["cid"] as String,
                        uid = doc.document["uid"] as String,
                        id = doc.document["id"] as String,
                        userName = doc.document["userName"] as String,
                        text = doc.document["text"] as String,
                        type = doc.document["type"] as String,
                        created_at = doc.document["created_at"] as String,
                        updated_at = doc.document["updated_at"] as String,
                    )

                    // 문서가 추가될 경우 추가
                    if (doc.type == DocumentChange.Type.ADDED)
                        items.add(chatMessage)

                    // 문서가 수정될 경우 수정 처리
                    if (doc.type == DocumentChange.Type.MODIFIED) {
                        items.get(cnt).text = chatMessage.text
                        items.get(cnt).updated_at = chatMessage.updated_at
                    }

                    // 문서가 삭제될 경우 삭제 처리
                    if (doc.type == DocumentChange.Type.REMOVED) {
                    }
                    cnt++
                }
            }
        } catch (exception: Exception) {
            Log.w("getChatRooms", "Error getting documents: ", exception)
        }

        return@withContext items
    }

    fun sendMessage(message: String, cid: String) {
        val time = getTime()
        val chatRef = db.collection("chatRooms").document(cid).collection("messages")

        val chatMessage = ChatMessage(
            cid = cid,
            uid = uid!!,
            userName = "이름",
            text = message,
            type = "msg",
            created_at = time,
            updated_at = time,
        )

        chatRef.add(chatMessage)
            .addOnSuccessListener {
                Log.d("msg", "DocumentSnapshot written with ID: ${it.id}")

                val updates = hashMapOf<String, Any>(
                    "id" to it.id,
                )

                chatRef.document(it.id).update(updates)
                    .addOnSuccessListener {
                        Log.d("msg", "DocumentSnapshot written with ID: ${it}")
                    }
                    .addOnFailureListener {e ->
                        Log.w("msg", "Error adding document", e)
                    }
            }
    }
}