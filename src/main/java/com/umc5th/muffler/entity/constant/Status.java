package com.umc5th.muffler.entity.constant;

public enum Status {
    ACTIVE, INACTIVE;

    public boolean isActive() {
        if (this.equals(ACTIVE)) {
            return true;
        }
        return false;
    }
}
