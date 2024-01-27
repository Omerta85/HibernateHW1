package task2;

import io.github.cdimascio.dotenv.Dotenv;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class CarDAO {

    private final SessionFactory sessionFactory;

    public CarDAO() {
        Dotenv dotenv = Dotenv.configure().load();
        String dbUrl = dotenv.get("DB_URL");
        String dbUsername = dotenv.get("DB_USERNAME");
        String dbPassword = dotenv.get("DB_PASSWORD");

        Configuration configuration = new Configuration()
                .setProperty("hibernate.connection.url", dbUrl)
                .setProperty("hibernate.connection.username", dbUsername)
                .setProperty("hibernate.connection.password", dbPassword)
                .setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect")
                .setProperty("hibernate.hbm2ddl.auto", "update")
                .addAnnotatedClass(Car.class);

        sessionFactory = configuration.buildSessionFactory();
    }

    public void saveCar(Car car) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.save(car);
            transaction.commit();
        }
    }

    public List<Car> getAllCars() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Car", Car.class).list();
        }
    }

    public static void main(String[] args) {
        CarDAO carDAO = new CarDAO();

        // Додавання авто в базу даних
        Car car1 = new Car("Toyota Camry", CarType.SEDAN, 200, 25000, 2022);
        carDAO.saveCar(car1);

        Car car2 = new Car("Honda CR-V", CarType.SUV, 180, 30000, 2022);
        carDAO.saveCar(car2);

        // Додавання ще 8 машин
        for (int i = 3; i <= 10; i++) {
            Car car = new Car("Brand" + i, CarType.SEDAN, 180 + i, 25000 + (i * 1000), 2022 - i);
            carDAO.saveCar(car);
        }

        // Дістання всіх авто
        List<Car> cars = carDAO.getAllCars();

        // Виведення авто
        for (Car car : cars) {
            System.out.println(car);
        }

        // Закриваємо sessionFactory
        carDAO.sessionFactory.close();
    }
}

