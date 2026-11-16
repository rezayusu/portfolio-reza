package dev.portfolio.content;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "portfolio_profile_photos")
public class ProfilePhoto {
    public static final String PRIMARY_SLOT = "profile";
    public static final String SECONDARY_SLOT = "profile-second";

    @Id
    @Column(length = 32)
    private String slot;

    @Column(nullable = false, length = 32)
    private String contentType;

    @JdbcTypeCode(SqlTypes.VARBINARY)
    @Column(nullable = false, columnDefinition = "bytea")
    private byte[] data;

    protected ProfilePhoto() {}

    public ProfilePhoto(String slot, String contentType, byte[] data) {
        this.slot = slot;
        this.contentType = contentType;
        this.data = data;
    }

    public String getSlot() { return slot; }
    public String getContentType() { return contentType; }
    public byte[] getData() { return data; }
}
