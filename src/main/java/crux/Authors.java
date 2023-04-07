package crux;

final class Authors {
  // TODO: Add author information.
  static final Author[] all = {new Author("Barr Avrahamov", "95213938", "bavraham"),
  new Author("Nicholas Correa", "73648921", "nrcorrea")};
}


final class Author {
  final String name;
  final String studentId;
  final String uciNetId;

  Author(String name, String studentId, String uciNetId) {
    this.name = name;
    this.studentId = studentId;
    this.uciNetId = uciNetId;
  }
}
