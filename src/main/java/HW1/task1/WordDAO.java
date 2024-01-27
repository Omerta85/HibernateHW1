package HW1.task1;

import HW1.task1.Word;
import io.github.cdimascio.dotenv.Dotenv;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;


import java.util.List;
import java.util.Properties;


public class WordDAO {

    private final SessionFactory sessionFactory;

    public WordDAO() {
        Dotenv dotenv = Dotenv.configure().load();
        String dbUrl = dotenv.get("DB_URL");
        String dbUsername = dotenv.get("DB_USERNAME");
        String dbPassword = dotenv.get("DB_PASSWORD");

        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.connection.url", dbUrl);
        hibernateProperties.setProperty("hibernate.connection.username", dbUsername);
        hibernateProperties.setProperty("hibernate.connection.password", dbPassword);
        hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "update");

        Configuration configuration = new Configuration()
                .addProperties(hibernateProperties)
                .addAnnotatedClass(Word.class);

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();

        sessionFactory = configuration.buildSessionFactory(registry);
    }


    public void saveWord(Word word) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.save(word);
            transaction.commit();
        }
    }

    public List<String> getAllWords() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("SELECT w.value FROM Word w", String.class).list();
        }
    }

    public static void main(String[] args) {
        WordDAO wordDAO = new WordDAO();

        // Додавання слова в базу даних
//        Word word = new Word();
//        word.setValue("Hello");
//        wordDAO.saveWord(word);
        try (Session session = wordDAO.sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            for (int i = 0; i < 10; i++) {
                Word word = new Word();
                word.setValue("Word" + i);
                session.save(word);
            }

            transaction.commit();
        }

        // Дістання всіх слів
        List<String> words = wordDAO.getAllWords();

        // Виведення слів
        for (String w : words) {
            System.out.println(w);
        }

        // Закриваємо sessionFactory
        wordDAO.sessionFactory.close();
    }
}
