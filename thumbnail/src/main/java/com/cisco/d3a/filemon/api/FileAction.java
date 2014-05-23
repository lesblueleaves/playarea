package com.cisco.d3a.filemon.api;

public enum FileAction {
    ADD(true, "add"),
    EDIT(true, "edit"),
    COPY(true, "copy"),
    MOVE(true, "move"),
    DELETE(false, "delete"),
    ADD_FILE(true, "add"),
    EDIT_FILE(true, "edit"),
    COPY_FILE(true, "copy"),
    MOVE_FILE(true, "move"),
    DELETE_FILE(false, "delete");

    private String action;

	private boolean fileRequired;

    public String action() {
        return action;
    }

	private FileAction(boolean fileRequired, String action) {
        this.fileRequired = fileRequired;
        this.action = action;
    }
	
	public boolean isFileRequired() {
		return this.fileRequired;
	}

    public static FileAction fromString(String action) {
        try {
            return FileAction.valueOf(action.toUpperCase());
        } catch(Exception e) {
            return null;
        }
    }
}
