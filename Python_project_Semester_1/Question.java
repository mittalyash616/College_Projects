import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Question {
    private String text;
    private String[] options;
    private int correctAnswer;

    public Question(String text, String[] options, int correctAnswer) {
        this.text = text;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }
    public String getText() {
        return text;
    }

    public String[] getOptions() {
        return options;
    }
    public int getCorrectAnswer() {
        return correctAnswer;
    }
    public static List<Question> getQuestions(Connection connection) {
        List<Question> questions = new ArrayList<>();
        questions.addAll(loadQuestionsFromDB(connection));
        Collections.shuffle(questions);
        return questions.size() > 15 ? questions.subList(0, 15) : questions;
    }
    private static List<Question> loadQuestionsFromDB(Connection connection) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT text, option_a, option_b, option_c, option_d, correct_answer FROM questions";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String text = rs.getString("text");
                String[] options = {
                        rs.getString("option_a"),
                        rs.getString("option_b"),
                        rs.getString("option_c"),
                        rs.getString("option_d")
                };
                int correctAnswer = rs.getInt("correct_answer");

                questions.add(new Question(text, options, correctAnswer));
            }
        } catch (SQLException e) {
            System.err.println("Error loading questions from DB: " + e.getMessage());
        }
        return questions;
    }
}