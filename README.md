# Проект второго модуля

## Структура проекта

Проект состоит из нескольких сервисов, каждый из которых выполняет отдельную задачу:

| Сервис                                   | Описание                                                              |
|------------------------------------------|-----------------------------------------------------------------------|
| [Blocklist producer](blocklist-producer) | Продюсер, публикующий списки заблокированных пользователей в Kafka.   |
| [Censure producer](censure-producer)     | Продюсер, публикующий запрещённые слова и допустимые замены в Kafka.  |
| [Message producer](message-producer)     | Продюсер, публикующий обычные сообщения между пользователями в Kafka. |
| [Filter processor](filter-processor)     | Потоковый процессор Kafka Streams для фильтрации и цензуры сообщений. |

## Описание классов

### Blocklist Producer

Расположен в `kz.yandex_practicum.kafka.blocklist_producer`.

| Класс                       | Описание                                          |
|-----------------------------|---------------------------------------------------|
| `Blocklist`                 | Модель списка заблокированных пользователей.      |
| `BlocklistProducerLauncher` | Отправляет списки блокировок в топик `blocklist`. |
| `JsonSerializer`            | Сериализация объекта `Blocklist` в JSON.          |

### Censure Producer

Расположен в `kz.yandex_practicum.kafka.censure_producer`.

| Класс                     | Описание                                                          |
|---------------------------|-------------------------------------------------------------------|
| `CensureProducerLauncher` | Отправляет пары (плохое слово → хорошее слово) в топик `censure`. |

### Message Producer

Расположен в `kz.yandex_practicum.kafka.message_producer`.

| Класс                     | Описание                                                   |
|---------------------------|------------------------------------------------------------|
| `Message`                 | Модель обычного текстового сообщения между пользователями. |
| `MessageProducerLauncher` | Отправляет сообщения в топик `message`.                    |
| `JsonSerializer`          | Сериализация объекта `Message` в JSON.                     |

### Filter Processor (Kafka Streams)

Расположен в `kz.yandex_practicum.kafka.filter_processor`.

| Класс                    | Описание                                               |
|--------------------------|--------------------------------------------------------|
| `MessageFilterProcessor` | Основной потоковый процессор для фильтрации и цензуры. |
| `Blocklist`              | Модель заблокированных пользователей.                  |
| `Message`                | Модель сообщения.                                      |
| `WordContext`            | Класс для обработки слов из сообщения.                 |
| `WordAccumulator`        | Класс для сборки итогового сообщения из слов.          |
| `OrderedWord`            | Вспомогательный класс для сортировки слов по порядку.  |
| `WordAccumulatorSerde`   | Сериализатор для аккумулятора слов.                    |
| `WordContextSerde`       | Сериализатор для контекста слова.                      |

## Логика работы приложения

1. **Blocklist Producer** публикует списки пользователей, заблокированных каждым пользователем.
2. **Censure Producer** публикует запрещённые слова и их допустимые замены.
3. **Message Producer** публикует сообщения между пользователями.
4. **Filter Processor**:
    - Фильтрует сообщения: блокирует сообщения от заблокированных пользователей.
    - Проверяет текст сообщения: заменяет запрещённые слова на допустимые.
    - Собирает очищенные сообщения и отправляет их в итоговый топик `filtered`.

---

## Инструкция по запуску

1. **Запустить Kafka-кластер:**

   ```sh
   docker-compose -f kafka-cluster-docker-compose.yml up -d
   ```

2. **Создать Kafka-топики:**

   ```sh
   docker exec -it kafka-broker-0 kafka-topics.sh --create --topic blocklist --bootstrap-server kafka-broker-0:9092 --partitions 3 --replication-factor 2
   ```

   ```sh
   docker exec -it kafka-broker-0 kafka-topics.sh --create --topic message --bootstrap-server kafka-broker-0:9092 --partitions 3 --replication-factor 2
   ```

   ```sh
   docker exec -it kafka-broker-0 kafka-topics.sh --create --topic censure --bootstrap-server kafka-broker-0:9092 --partitions 3 --replication-factor 2
   ```

   ```sh
   docker exec -it kafka-broker-0 kafka-topics.sh --create --topic filtered --bootstrap-server kafka-broker-0:9092 --partitions 3 --replication-factor 2
   ```

3. **Запустить сервисы:**

   ```sh
   docker-compose -f java-services-docker-compose.yml up --build -d
   ```

## Проверка работы

1. Открыть Kafka UI на
   (http://localhost:8080/ui/clusters/project-1-cluster/all-topics/filtered/messages?filterQueryType=STRING_CONTAINS&attempt=2&limit=100&page=0&seekDirection=BACKWARD&keySerde=String&valueSerde=String&seekType=LATEST)
   и убедиться, что сообщения поступают в топик `filtered`.

2. Убедиться, что сообщения корректно публикуются. Проверить логи консьюмеров:
   ```sh
   docker compose -f java-services-docker-compose.yml logs -f
   ```

## Завершение работы

Чтобы остановить все сервисы, выполните:

   ```sh
      docker-compose -f java-services-docker-compose.yml down
   ```

   ```sh
      docker-compose -f kafka-cluster-docker-compose.yml down
   ```