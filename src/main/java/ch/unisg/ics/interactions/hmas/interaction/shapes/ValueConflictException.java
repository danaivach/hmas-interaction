package ch.unisg.ics.interactions.hmas.interaction.shapes;

class ValueConflictException extends IllegalArgumentException {
  public ValueConflictException(String value, String defaultValue) {
    super("The specified value (" + value + ") has a conflict with the specified default value (" + defaultValue + ").");
  }
}