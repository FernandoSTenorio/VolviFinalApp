
package com.fernandotenorio.volvifinalapp.Model;

        import com.fernandotenorio.volvifinalapp.Utils.DataSnapshotPrinter;
        import com.google.firebase.database.DataSnapshot;

        import java.util.HashMap;
        import java.util.Map;

public class Comment {

    private final String id;
    private final String blogId;
    private final String authorId;
    private final String author;
    private final String message;
    private final long createdDate;

    public Comment(final DataSnapshot snapshot) {

        DataSnapshotPrinter printer = new DataSnapshotPrinter(snapshot);

        this.id = snapshot.getKey();
        this.blogId = printer.print("blogId", String.class);
        this.authorId = printer.print("authorId", String.class);
        this.author = printer.print("author", String.class);
        this.message = printer.print("message", String.class);
        this.createdDate = printer.print("createdDate", Long.class);
    }

    public Comment(final String id, final String blogId, final String authorId, final String author, final String message, final long createdDate) {
        this.id = id;
        this.blogId = blogId;
        this.authorId = authorId;
        this.author = author;
        this.createdDate = createdDate;
        this.message = message;
    }

    public String id() {
        return id;
    }

    public String blogId() {
        return blogId;
    }

    public String authorId() {
        return authorId;
    }

    public String author() {
        return author;
    }

    public String message() {
        return message;
    }

    public long createdDate() {
        return createdDate;
    }

    Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("blogId", blogId);
        map.put("authorId", authorId);
        map.put("author", author);
        map.put("message", message);
        map.put("createdDate", System.currentTimeMillis());

        return map;
    }
}
