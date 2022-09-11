import java.lang.RuntimeException

data class Post(
    val id: Int = 0,
    val ownerId: Int,
    val fromId: Int = ownerId,
    val createdBy: Int = ownerId,
    val date: Int = 0,
    val text: String,
    val replyOwnerId: Int = 0,
    val replyPostId: Int = 0,
    val friendsOnly: Boolean = true,
    val comments: Any? = null,
    val copyright: Any? = null,
    val likes: Likes = Likes(0),
    val reposts: Any? = null,
    val views: Int = 0,
    val postType: String = "post",
    val postSource: Any? = null,
    val attachments: Array<Attachment>,
    val geo: Any? = null,
    val signerId: Int = ownerId,
    val copyHistory: Any? = null,
    val canPin: Boolean = false,
    val canDelete: Boolean = false,
    val canEdit: Boolean = false,
    val isPinned: Boolean = false,
    val markedAsAds: Boolean = false,
    val isFavorite: Boolean = false,
    val donut: Any? = null,
    val postponedId: Int = 0
) {
    class Likes(val count: Int, userLikes: Boolean = false)
}

object WallService {
    private var posts = emptyArray<Post>()
    private var comments = emptyArray<Comment>()
    private var id = 1

    fun clear() {
        posts = emptyArray()
    }

    fun add(post: Post): Post {
        posts += post.copy(id = id)
        id += 1
        return posts.last()
    }

    fun update(post: Post): Boolean {
        for ((index, value) in posts.withIndex()) {
            if (value.id == post.id) {
                posts[index] = post.copy(ownerId = value.ownerId, date = value.date)
                return true
            }
        }
        return false
    }

    fun createComment(postId: Int, comment: Comment): Comment {
        var post: Post? = null
        for (i in posts) {
            if (i.id == postId) post = i
        }
        if (post == null) {
            throw PostNotFoundException("no post with id $postId")
        } else {
            comments += comment
            return comments.last()
        }
    }
}

class PostNotFoundException(message: String) : RuntimeException(message)

data class Comment(
    val id: Int,
    val fromId: Int,
    val date: Int,
    val text: String,
    val donut: Any? = null,
    val replyToUser: Int,
    val replyToComment: Int,
    val attachments: Any? = null,
    val parentsStack: Any? = null,
    val thread: Any? = null
)

abstract class Attachment(val type: String)

data class Photo(
    val id: Int,
    val albumId: Int,
    val ownerId: Int,
    val userId: Int,
    val text: String,
    val date: Int,
    val sizes: Any? = null,
    val width: Int,
    val height: Int
)

class PhotoAttachment(val photo: Photo) : Attachment("photo")

data class Video(
    val id: Int,
    val ownerId: Int,
    val title: String,
    val description: String,
    val duration: Int,
    val image: Any?,
    val firstFrame: Any?,
    val date: Int,
    val addingDate: Int,
    val views: Int,
    val localViews: Int,
    val comments: Int,
    val player: String,
    val platform: String,
    val canAdd: Boolean,
    val isPrivate: Boolean,
    val accessKey: String,
    val processing: Int,
    val isFavorite: Boolean,
    val canComment: Boolean,
    val canEdit: Boolean,
    val canLike: Boolean,
    val canRepost: Boolean,
    val canSubscribe: Boolean,
    val canAddToFaves: Boolean,
    val canAttachLink: Boolean,
    val width: Int,
    val height: Int,
    val userId: Int,
    val converting: Boolean,
    val added: Boolean,
    val isSubscribed: Boolean,
    val repeat: Int,
    val type: String,
    val balance: Int,
    val liveStatus: String,
    val live: Int,
    val upcoming: Int,
    val spectators: Int,
    val likes: Any?,
    val reposts: Any?
)

class VideoAttachment(val video: Video) : Attachment("video")

data class Audio(
    val id: Int,
    val ownerId: Int,
    val artist: String,
    val title: String,
    val duration: Int,
    val url: String,
    val lyricsId: Int,
    val albumId: Int,
    val genreId: Int,
    val date: Int,
    val noSearch: Int,
    val isHq: Int
)

class AudioAttachment(val audio: Audio) : Attachment("audio")

data class Doc(
    val id: Int,
    val ownerId: Int,
    val title: String,
    val size: Int,
    val ext: String,
    val url: String,
    val date: Int,
    val type: Int,
    val preview: Any? = null
)

class DocAttachment(val doc: Doc) : Attachment("doc")

data class Link(
    val url: String,
    val title: String,
    val caption: String,
    val description: String,
    val photo: Any? = null,
    val product: Any? = null,
    val button: Any? = null,
    val previewPage: String,
    val previewUrl: String
)

class LinkAttachment(val link: Link) : Attachment("link")

data class Note(
    val nid: Int = 0,
    val title: String,
    val text: String,
    val privacy: Int,
    val commentPrivacy: Int,
    val privacyView: String,
    val privacyComment: String,
    var isDelete: Boolean = false
)

data class NoteComment(
    val cid: Int,
    val noteId: Int,
    val ownerId: Int,
    val replyTo: Int,
    val message: String,
    val guid: String,
    var isDelete: Boolean = false
)

object NoteService {
    private var notes = mutableListOf<Note>()
    private var comments = mutableListOf<NoteComment>()
    private var nid = 1
    private var cid = 1

    fun clear() {
        notes = mutableListOf()
        comments = mutableListOf()
        nid = 1
        cid = 1
    }

    fun add(
        title: String,
        text: String,
        privacy: Int = 0,
        commentPrivacy: Int = 0,
        privacyView: String = "all",
        privacyComment: String = "all"
    ): Int {
        val note = Note(
            nid = nid,
            title = title,
            text = text,
            privacy = privacy,
            commentPrivacy = commentPrivacy,
            privacyView = privacyView,
            privacyComment = privacyComment
        )
        notes += note
        nid += 1
        return note.nid
    }

    fun createComment(noteId: Int, ownerId: Int = 0, replyTo: Int = 0, message: String, guid: String = "guid"): Int {
        for (i in notes) {
            if (i.nid == noteId && !i.isDelete) {
                val comment = NoteComment(
                    cid = cid, noteId = noteId, ownerId = ownerId, replyTo = replyTo, message = message, guid = guid
                )
                comments += comment
                cid += 1
                return comment.cid
            }
        }
        return 0
    }

    fun delete(noteId: Int): Int {
        for (i in notes) {
            if (i.nid == noteId) {
                i.isDelete = true
                for (k in comments) {
                    if (k.noteId == noteId) k.isDelete = true
                }
                return 1
            }
        }
        return 0
    }

    fun deleteComment(commentId: Int, ownerId: Int = 0): Int {
        for (i in comments) {
            if (i.cid == commentId && i.ownerId == ownerId) {
                i.isDelete = true
                return 1
            }
        }
        return 0
    }

    fun edit(
        noteId: Int,
        title: String,
        text: String,
        privacy: Int = 0,
        commentPrivacy: Int = 0,
        privacyView: String = "all",
        privacyComment: String = "all"
    ): Int {
        val updateNote = Note(
            nid = noteId,
            title = title,
            text = text,
            privacy = privacy,
            commentPrivacy = commentPrivacy,
            privacyView = privacyView,
            privacyComment = privacyComment
        )
        for ((index, item) in notes.withIndex()) {
            if (item.nid == updateNote.nid && !item.isDelete) {
                notes[index] = updateNote
                return 1
            }
        }
        return 0
    }

    fun editComment(commentId: Int, ownerId: Int = 0, message: String): Int {
        for ((index, item) in comments.withIndex()) {
            if (item.cid == commentId && !item.isDelete) {
                comments[index] = item.copy(cid = commentId, ownerId = ownerId, message = message)
                return 1
            }
        }
        return 0
    }

    fun get(noteIds: String, userId: Int = 0, offset: Int = 0, count: Int = 0, sort: Int = 0): List<Note> {
        val result = mutableListOf<Note>()
        val listNoteIds = noteIds.split(" ")
        val intListNoteIds = mutableListOf<Int>()
        for (i in listNoteIds) intListNoteIds.add(i.toInt())
        for (i in notes) {
            if (i.nid in intListNoteIds && !i.isDelete) result.add(i)
        }
        return result
    }

    fun getById(noteId: Int, ownerId: Int = 0, needWiki: Any? = null): List<Note> {
        val result = mutableListOf<Note>()
        for (i in notes) {
            if (i.nid == noteId && !i.isDelete) result.add(i)
        }
        return result
    }

    fun getComments(noteId: Int, ownerId: Int = 0, sort: Int = 0, offset: Int = 0, count: Int = 0): List<NoteComment> {
        val result = mutableListOf<NoteComment>()
        for (i in comments) {
            if (i.noteId == noteId && !i.isDelete) result.add(i)
        }
        return result
    }

    fun restoreComment(commentId: Int, ownerId: Int = 0): Int {
        for (i in comments) {
            if (i.cid == commentId && i.ownerId == ownerId && i.isDelete && !isDeleteNote(i.noteId)) {
                i.isDelete = false
                return 1
            }
        }
        return 0
    }

    private fun isDeleteNote(noteId: Int): Boolean {
        for (i in notes) if (i.nid == noteId && i.isDelete) return true
        return false
    }
}

fun main() {
//    val photo = Photo(1, 1, 1, 1, "text", 0, null, 0, 0)
//    val doc = Doc(1, 1, "title", 5, "docx", "url", 0, 1)
//    val link = Link("url", "title", "caption", "description", previewPage = "page", previewUrl = "url")
//    val firstPost = Post(
//        ownerId = 44,
//        date = 1661345460,
//        text = "First post!",
//        attachments = arrayOf(PhotoAttachment(photo), DocAttachment(doc))
//    )
//    val secondPost = Post(
//        ownerId = 44,
//        date = 1661367175,
//        text = "Second post!",
//        attachments = arrayOf(LinkAttachment(link), PhotoAttachment(photo))
//    )
//    val updatePost = Post(
//        id = 2,
//        ownerId = 44,
//        date = 1661374009,
//        text = "Update",
//        attachments = arrayOf(DocAttachment(doc), LinkAttachment(link))
//    )
//    val comment = Comment(id = 1, fromId = 1, date = 1, text = "Hello", replyToUser = 1, replyToComment = 0)
//
//    println(WallService.add(firstPost))
//    println(WallService.add(secondPost))
//    WallService.update(updatePost)
//    println(WallService.createComment(1, comment))

    NoteService.add(title = "First", text = "First note!")
    NoteService.add(title = "Second", text = "Second note!")
    NoteService.createComment(1, message = "Wow")
    NoteService.createComment(2, message = "Nice")
    NoteService.createComment(2, message = "Finally")
    NoteService.delete(1)
    NoteService.deleteComment(3)

    NoteService.edit(2, title = "Sec", text = "Sec note")
    NoteService.editComment(2, message = "Nice!")
    NoteService.restoreComment(3)

    println(NoteService.get("1 2"))
    println(NoteService.getComments(2))

}