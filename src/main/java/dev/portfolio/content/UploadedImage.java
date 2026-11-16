package dev.portfolio.content;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "portfolio_uploaded_images")
public class UploadedImage {
    @Id
    @Column(length = 100)
    private String filename;

    @Column(nullable = false, length = 32)
    private String contentType;

    @JdbcTypeCode(SqlTypes.VARBINARY)
    @Column(nullable = false, columnDefinition = "bytea")
    private byte[] data;

    protected UploadedImage() {}

    public UploadedImage(String filename, String contentType, byte[] data) {
        this.filename = filename;
        this.contentType = contentType;
        this.data = data;
    }

    public String getFilename() { return filename; }
    public String getContentType() { return contentType; }
    public byte[] getData() { return data; }
}
