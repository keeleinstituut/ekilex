package eki.ekilex.data;

import java.util.List;

public class NoteLangGroup extends LangGroup {

	private static final long serialVersionUID = 1L;

	private List<Note> notes;

	public List<Note> getNotes() {
		return notes;
	}

	public void setNotes(List<Note> notes) {
		this.notes = notes;
	}
}