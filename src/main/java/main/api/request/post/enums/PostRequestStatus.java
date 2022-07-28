package main.api.request.post.enums;

import main.model.ModerationStatus;

public enum PostRequestStatus {
    INACTIVE(null),
    PENDING(ModerationStatus.NEW),
    NEW(ModerationStatus.NEW),
    DECLINED(ModerationStatus.DECLINED),
    PUBLISHED(ModerationStatus.ACCEPTED),
    ACCEPTED(ModerationStatus.ACCEPTED);

    private final ModerationStatus moderationStatus;

    PostRequestStatus(ModerationStatus moderationStatus) {
        this.moderationStatus = moderationStatus;
    }

    public ModerationStatus getModerationStatus() {
        return moderationStatus;
    }
}
