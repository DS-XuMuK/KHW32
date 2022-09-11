import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class NoteServiceTest {

    @Before
    fun clearBeforeTest() {
        NoteService.clear()
    }

    @Test
    fun add() {
        val result = NoteService.add("title", "text")
        assertEquals(1, result)
    }

    @Test
    fun createCommentNotDeletedNote() {
        NoteService.add("title", "text")
        val result = NoteService.createComment(1, message = "message")
        assertEquals(1, result)
    }

    @Test
    fun createCommentDeletedNote() {
        NoteService.add("title", "text")
        NoteService.delete(1)
        val result = NoteService.createComment(1, message = "message")
        assertEquals(0, result)
    }

    @Test
    fun deleteExistsNote() {
        NoteService.add("title", "text")
        val result = NoteService.delete(1)
        assertEquals(1, result)
    }

    @Test
    fun deleteNotExistsNote() {
        val result = NoteService.delete(Int.MAX_VALUE)
        assertEquals(0, result)
    }

    @Test
    fun deleteCommentExists() {
        NoteService.add("title", "text")
        NoteService.createComment(1, message = "message")
        val result = NoteService.deleteComment(1)
        assertEquals(1, result)
    }

    @Test
    fun deleteCommentNotExists() {
        NoteService.add("title", "text")
        NoteService.createComment(1, message = "message")
        val result = NoteService.deleteComment(Int.MAX_VALUE)
        assertEquals(0, result)
    }

    @Test
    fun editNotDeletedNote() {
        NoteService.add("title", "text")
        val result = NoteService.edit(1, title = "new_title", text = "new_text")
        assertEquals(1, result)
    }

    @Test
    fun editDeletedNote() {
        NoteService.add("title", "text")
        NoteService.delete(1)
        val result = NoteService.edit(1, title = "new_title", text = "new_text")
        assertEquals(0, result)
    }

    @Test
    fun editCommentNotDeletedNote() {
        NoteService.add("title", "text")
        NoteService.createComment(1, message = "message")
        val result = NoteService.editComment(1, message = "new_message")
        assertEquals(1, result)
    }

    @Test
    fun editCommentDeletedNote() {
        NoteService.add("title", "text")
        NoteService.createComment(1, message = "message")
        NoteService.delete(1)
        val result = NoteService.editComment(1, message = "new_message")
        assertEquals(0, result)
    }

    @Test
    fun get() {
        NoteService.add("title", "text")
        NoteService.add("title2", "text2")
        val result = NoteService.get("1 2")
        assertEquals(
            listOf(
                Note(1, "title", "text", 0, 0, "all", "all"),
                Note(2, "title2", "text2", 0, 0, "all", "all")
            ), result
        )
    }

    @Test
    fun getById() {
        NoteService.add("title", "text")
        val result = NoteService.getById(1)
        assertEquals(listOf(Note(1, "title", "text", 0, 0, "all", "all")), result)
    }

    @Test
    fun getComments() {
        NoteService.add("title", "text")
        NoteService.createComment(1, message = "message")
        NoteService.createComment(1, message = "message2")
        val result = NoteService.getComments(1)
        assertEquals(
            listOf(
                NoteComment(1, 1, 0, 0, "message", "guid", false),
                NoteComment(2, 1, 0, 0, "message2", "guid", false)
            ), result
        )
    }

    @Test
    fun restoreCommentDelete() {
        NoteService.add("title", "text")
        NoteService.createComment(1, message = "message")
        NoteService.deleteComment(1)
        val result = NoteService.restoreComment(1)
        assertEquals(1, result)
    }

    @Test
    fun restoreCommentNotDelete() {
        NoteService.add("title", "text")
        NoteService.createComment(1, message = "message")
        val result = NoteService.restoreComment(1)
        assertEquals(0, result)
    }
}