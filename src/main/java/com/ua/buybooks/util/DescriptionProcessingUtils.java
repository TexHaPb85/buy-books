package com.ua.buybooks.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DescriptionProcessingUtils {
    public static String processDescriptionRU(String description, String category) {
        if (description == null) {
            return null;
        }

        description = description.trim().replaceFirst("<p>Описание книги<br />\n", "<p>");

        Set<String> replacedWords = new HashSet<>();

        for (Map.Entry<String, String> entry : SYNONYMS_RU.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (!replacedWords.contains(key) && !replacedWords.contains(value)) {
                if (description.contains(key)) {
                    description = description.replace(key, value);
                    replacedWords.add(value);
                }
            }
        }

        for (Map.Entry<String, String> entry : SYNONYMS_RU.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (!replacedWords.contains(key) && !replacedWords.contains(value)) {
                if (description.contains(value)) {
                    description = description.replace(value, key);
                    replacedWords.add(key);
                }
            }
        }

        description = addDescriptionEndingRu(description, category);

        return description;
    }

    public static String addDescriptionEndingRu(String description, String categoryName) {
        StringBuilder descriptionBuilder = new StringBuilder(description);
        descriptionBuilder.append("<p>Заказывайте этот товар прямо сейчас и получайте скидку на следующую покупку!");
        if (categoryName != null && !categoryName.isEmpty()) {
            descriptionBuilder.append("<p>Больше товаров категории ").append(categoryName).append(" в интернет-магазине buy-books.com.ua</p>");
        } else {
            descriptionBuilder.append("<p>Больше товаров в интернет-магазине buy-books.com.ua</p>");
        }

        return descriptionBuilder.toString();
    }

    public static String processDescriptionUA(String description, String category) {
        if (description == null) {
            return null;
        }
        description = description.trim().replaceFirst("<p>Опис книги<br />\n", "<p>");

        Set<String> replacedWords = new HashSet<>();

        // Replace keys with values
        for (Map.Entry<String, String> entry : SYNONYMS_UA.entrySet()) {
            if (!replacedWords.contains(entry.getKey()) && !replacedWords.contains(entry.getValue())) {
                if (description.contains(entry.getKey())) {
                    description = description.replace(entry.getKey(), entry.getValue());
                    replacedWords.add(entry.getValue());
                }
            }
        }

        // Replace values with keys
        for (Map.Entry<String, String> entry : SYNONYMS_UA.entrySet()) {
            if (!replacedWords.contains(entry.getKey()) && !replacedWords.contains(entry.getValue())) {
                if (description.contains(entry.getValue())) {
                    description = description.replace(entry.getValue(), entry.getKey());
                    replacedWords.add(entry.getKey());
                }
            }
        }
        String actionText = "Замовляйте цей товар зараз та отримуйте знижку на наступну покупку!";
        description += "\n" + actionText;
        //TODO: category href link
        // Add additional text about more products
        if (category != null && !category.isEmpty()) {
            String additionalText = "<p>Більше товарів категорії " + category + " в інтернет магазині buy-books.com.ua</p>";
            description += "\n" + additionalText;
        } else {
            String additionalText = "<p>Більше товарів в інтернет магазині buy-books.com.ua</p>";
            description += "\n" + additionalText;
        }

        return description;
    }

    private static final Map<String, String> SYNONYMS_RU = new HashMap<>();

    private static final Map<String, String> SYNONYMS_UA = new HashMap<>();

    static {
        SYNONYMS_UA.put("клієнтів", "споживачів");
        SYNONYMS_UA.put("компаній", "фірм");
        SYNONYMS_UA.put("даних", "інформації");
        SYNONYMS_UA.put("прибутковості", "доходу");
        SYNONYMS_UA.put("аналіз", "дослідження");
        SYNONYMS_UA.put("вивчили", "дослідили");
        SYNONYMS_UA.put("лояльності", "вірності");
        SYNONYMS_UA.put("покупців", "замовників");
        SYNONYMS_UA.put("виручки", "доходів");
        SYNONYMS_UA.put("детальний", "розгорнутий");
        SYNONYMS_UA.put("посібник", "довідник");
        SYNONYMS_UA.put("технік", "методик");
        SYNONYMS_UA.put("ритуалів", "обрядів");
        SYNONYMS_UA.put("внутрішню", "приховану");
        SYNONYMS_UA.put("енергію", "силу");
        SYNONYMS_UA.put("управління", "керування");
        SYNONYMS_UA.put("стихією", "елементом");
        SYNONYMS_UA.put("вогню", "полум'я");
        SYNONYMS_UA.put("теоретичне", "концептуальне");
        SYNONYMS_UA.put("підґрунтя", "основу");
        SYNONYMS_UA.put("техніки", "методики");
        SYNONYMS_UA.put("накопичення", "збирання");
        SYNONYMS_UA.put("контролю", "керування");
        SYNONYMS_UA.put("практичні", "прикладні");
        SYNONYMS_UA.put("вправи", "завдання");
        SYNONYMS_UA.put("фаєрболів", "вогняних куль");
        SYNONYMS_UA.put("методи", "способи");
        SYNONYMS_UA.put("захисту", "оборони");
        SYNONYMS_UA.put("безпечного", "убезпеченого");
        SYNONYMS_UA.put("використання", "застосування");
        SYNONYMS_UA.put("курс", "програма");
        SYNONYMS_UA.put("новачків", "початківців");
        SYNONYMS_UA.put("досвідчених", "кваліфікованих");
        SYNONYMS_UA.put("практиків", "майстрів");
        SYNONYMS_UA.put("покрокові", "детальні");
        SYNONYMS_UA.put("інструкції", "настанови");
        SYNONYMS_UA.put("поради", "рекомендації");
        SYNONYMS_UA.put("шлях", "дорога");
        SYNONYMS_UA.put("вивченні", "опануванні");
        SYNONYMS_UA.put("магії", "чарівництва");
        SYNONYMS_UA.put("енергетика", "силова база");
        SYNONYMS_UA.put("чакри", "енергетичні центри");
        SYNONYMS_UA.put("візуалізація", "уявлення");
        SYNONYMS_UA.put("медитація", "заглиблення");
        SYNONYMS_UA.put("вогняна", "пекельна");
        SYNONYMS_UA.put("стихія", "елемент");
        SYNONYMS_UA.put("символіка", "знакова система");
        SYNONYMS_UA.put("нетрадиційними", "альтернативними");
        SYNONYMS_UA.put("практиками", "методами");
        SYNONYMS_UA.put("заглибитися", "зануритися");
        SYNONYMS_UA.put("магічний", "чарівний");
        SYNONYMS_UA.put("унікальна", "неповторна");
        SYNONYMS_UA.put("антологія", "збірка");
        SYNONYMS_UA.put("духовні", "релігійні");
        SYNONYMS_UA.put("вчення", "доктрини");
        SYNONYMS_UA.put("культур", "традицій");
        SYNONYMS_UA.put("релігій", "віровчень");
        SYNONYMS_UA.put("присвячені", "спрямовані");
        SYNONYMS_UA.put("темам", "питанням");
        SYNONYMS_UA.put("смерті", "відходу");
        SYNONYMS_UA.put("вмирання", "фізичного згасання");
        SYNONYMS_UA.put("приймати", "сприймати");
        SYNONYMS_UA.put("життєвого", "екзистенційного");
        SYNONYMS_UA.put("шляху", "дороги");
        SYNONYMS_UA.put("пропонує", "надає");
        SYNONYMS_UA.put("роздуми", "міркування");
        SYNONYMS_UA.put("значення", "сенс");
        SYNONYMS_UA.put("символіку", "знаковий зміст");
        SYNONYMS_UA.put("допомогти", "сприяти");
        SYNONYMS_UA.put("подолати", "побороти");
        SYNONYMS_UA.put("страх", "переляк");
        SYNONYMS_UA.put("тривогу", "занепокоєння");
        SYNONYMS_UA.put("невідомим", "небаченим");
        SYNONYMS_UA.put("екстрасенсорних", "паранормальних");
        SYNONYMS_UA.put("здібностей", "можливостей");
        SYNONYMS_UA.put("1930-х", "тридцятих");
        SYNONYMS_UA.put("символів", "знаків");
        SYNONYMS_UA.put("коло", "обвід");
        SYNONYMS_UA.put("хрест", "перехрестя");
        SYNONYMS_UA.put("хвилясті", "змієподібні");
        SYNONYMS_UA.put("лінії", "полоси");
        SYNONYMS_UA.put("квадрат", "чотирикутник");
        SYNONYMS_UA.put("застосовуються", "використовуються");
        SYNONYMS_UA.put("дослідженнях", "наукових працях");
        SYNONYMS_UA.put("іграх", "розвагах");
        SYNONYMS_UA.put("тренуваннях", "практикумах");
        SYNONYMS_UA.put("інтуїції", "відчуття");
        SYNONYMS_UA.put("захоплююча", "цікава");
        SYNONYMS_UA.put("колода", "набір карт");
        SYNONYMS_UA.put("представляє", "демонструє");
        SYNONYMS_UA.put("унікальне", "особливе");
        SYNONYMS_UA.put("бачення", "сприйняття");
        SYNONYMS_UA.put("традиційного", "класичного");
        SYNONYMS_UA.put("події", "обставини");
        SYNONYMS_UA.put("класичній", "канонічній");
        SYNONYMS_UA.put("інструментом", "засобом");
        SYNONYMS_UA.put("архетипів", "первісних образів");
        SYNONYMS_UA.put("символів", "значків");
        SYNONYMS_UA.put("ситуацій", "обставин");
        SYNONYMS_UA.put("ракурс", "кут зору");
        SYNONYMS_UA.put("інтерпретації", "тлумачення");
        SYNONYMS_UA.put("Вінтажне", "Ретро");
        SYNONYMS_UA.put("класична", "традиційна");
        SYNONYMS_UA.put("злегка", "ледве");
        SYNONYMS_UA.put("ретро-оформленні", "ретро-стилі");
        SYNONYMS_UA.put("створюючи", "викликаючи");
        SYNONYMS_UA.put("відчуття", "враження");
        SYNONYMS_UA.put("старовини", "давнини");
        SYNONYMS_UA.put("антикваріату", "антикварності");
        SYNONYMS_UA.put("особливої", "виняткової");
        SYNONYMS_UA.put("атмосфери", "аури");
        SYNONYMS_UA.put("глибини", "значущості");
        SYNONYMS_UA.put("любителями", "прихильниками");
        SYNONYMS_UA.put("колекціонерами", "збирачами");
        SYNONYMS_UA.put("імітує", "наслідує");
        SYNONYMS_UA.put("знайдена", "відкрита");
        SYNONYMS_UA.put("оригінальному", "автентичному");
        SYNONYMS_UA.put("легко", "просто");
        SYNONYMS_UA.put("відомою", "упізнаваною");
        SYNONYMS_UA.put("розмір", "формат");
        SYNONYMS_UA.put("стандартному", "типовому");
        SYNONYMS_UA.put("іноді", "часом");
        SYNONYMS_UA.put("щільному", "товстому");
        SYNONYMS_UA.put("матовому", "неглянцевому");
        SYNONYMS_UA.put("папері", "картоні");
        SYNONYMS_UA.put("приємними", "комфортними");
        SYNONYMS_UA.put("дотик", "доторк");
        SYNONYMS_UA.put("стійкими", "витривалими");
        SYNONYMS_UA.put("частого", "постійного");
        SYNONYMS_UA.put("новачки", "початківці");
        SYNONYMS_UA.put("вінтажне", "ретро");
        SYNONYMS_UA.put("злегка", "ледве");
        SYNONYMS_UA.put("зображення", "ілюстрації");
        SYNONYMS_UA.put("зістареному", "ретро-оформленому");
        SYNONYMS_UA.put("скрині", "сховку");
        SYNONYMS_UA.put("обмеженим", "лімітованим");
        SYNONYMS_UA.put("тиражем", "накладом");
        SYNONYMS_UA.put("альтернативні", "інші");
        SYNONYMS_UA.put("посібник", "довідник");
        SYNONYMS_UA.put("буклет", "брошура");
        SYNONYMS_UA.put("корисним", "потрібним");
        SYNONYMS_UA.put("любителі", "прихильники");
        SYNONYMS_UA.put("класики", "традиційних варіантів");
        SYNONYMS_UA.put("автентичному", "справжньому");
        SYNONYMS_UA.put("вигляді", "формі");
        SYNONYMS_UA.put("колекціонери", "збирачі");
        SYNONYMS_UA.put("обмежених", "рідкісних");
        SYNONYMS_UA.put("виданнях", "публікаціях");
        SYNONYMS_UA.put("свій", "власний");
        SYNONYMS_UA.put("шлях", "маршрут");
        SYNONYMS_UA.put("автор", "творець");
        SYNONYMS_UA.put("агент", "представник");
        SYNONYMS_UA.put("адвокат", "захисник");
        SYNONYMS_UA.put("адреса", "локація");
        SYNONYMS_UA.put("акцент", "наголос");
        SYNONYMS_UA.put("акція", "розпродаж");
        SYNONYMS_UA.put("алкоголь", "спиртне");
        SYNONYMS_UA.put("аналізувати", "розглядати");
        SYNONYMS_UA.put("ансамбль", "колектив");
        SYNONYMS_UA.put("аптека", "фармація");
        SYNONYMS_UA.put("архів", "сховище");
        SYNONYMS_UA.put("афіша", "оголошення");
        SYNONYMS_UA.put("баланс", "рівновага");
        SYNONYMS_UA.put("банк", "фінустанова");
        SYNONYMS_UA.put("берег", "узбережжя");
        SYNONYMS_UA.put("бідність", "убогість");
        SYNONYMS_UA.put("білизна", "натільна тканина");
        SYNONYMS_UA.put("благодійність", "милосердя");
        SYNONYMS_UA.put("близький", "поруч");
        SYNONYMS_UA.put("блог", "щоденник");

        SYNONYMS_UA.put("біль", "страждання");
        SYNONYMS_UA.put("борг", "заборгованість");
        SYNONYMS_UA.put("боротьба", "змагання");
        SYNONYMS_UA.put("будівля", "споруда");
        SYNONYMS_UA.put("буття", "існування");
        SYNONYMS_UA.put("ведучий", "модератор");
        SYNONYMS_UA.put("вершина", "пік");
        SYNONYMS_UA.put("взагалі", "загалом");
        SYNONYMS_UA.put("відгук", "рецензія");
        SYNONYMS_UA.put("відомий", "славетний");
        SYNONYMS_UA.put("відстань", "дистанція");
        SYNONYMS_UA.put("віра", "переконання");
        SYNONYMS_UA.put("вісім", "октет");
        SYNONYMS_UA.put("влада", "уряд");
        SYNONYMS_UA.put("вміння", "здатність");
        SYNONYMS_UA.put("водій", "кермувальник");
        SYNONYMS_UA.put("воїн", "вояк");
        SYNONYMS_UA.put("воля", "свобода");
        SYNONYMS_UA.put("волонтер", "доброволець");

        SYNONYMS_UA.put("газета", "періодика");
        SYNONYMS_UA.put("гарантія", "запорука");
        SYNONYMS_UA.put("геніальний", "талановитий");
        SYNONYMS_UA.put("герой", "звитяжець");
        SYNONYMS_UA.put("гордість", "пиша");
        SYNONYMS_UA.put("готель", "гостиниця");
        SYNONYMS_UA.put("гроші", "кошти");
        SYNONYMS_UA.put("громада", "спільнота");
        SYNONYMS_UA.put("два", "пара");
        SYNONYMS_UA.put("двір", "подвір’я");
        SYNONYMS_UA.put("держава", "країна");
        SYNONYMS_UA.put("дизайн", "стилізація");
        SYNONYMS_UA.put("діалог", "бесіда");
        SYNONYMS_UA.put("діловий", "бізнесовий");
        SYNONYMS_UA.put("діти", "малюки");
        SYNONYMS_UA.put("дозвіл", "погодження");
        SYNONYMS_UA.put("довіра", "впевненість");
        SYNONYMS_UA.put("договір", "угода");
        SYNONYMS_UA.put("доказ", "аргумент");

        SYNONYMS_UA.put("докладно", "детально");
        SYNONYMS_UA.put("допомога", "підтримка");
        SYNONYMS_UA.put("дорожній", "транспортний");
        SYNONYMS_UA.put("досягнення", "успіх");
        SYNONYMS_UA.put("дурість", "глупота");
        SYNONYMS_UA.put("ерудит", "знавець");
        SYNONYMS_UA.put("етап", "стадія");
        SYNONYMS_UA.put("жарт", "кепкування");
        SYNONYMS_UA.put("жінка", "пані");
        SYNONYMS_UA.put("житло", "помешкання");
        SYNONYMS_UA.put("жовтий", "сонячний");
        SYNONYMS_UA.put("завод", "фабрика");
        SYNONYMS_UA.put("завтра", "наступного дня");
        SYNONYMS_UA.put("загроза", "небезпека");
        SYNONYMS_UA.put("зайвий", "непотрібний");
        SYNONYMS_UA.put("запис", "реєстрація");
        SYNONYMS_UA.put("заробітна плата", "оклад");
        SYNONYMS_UA.put("звичай", "обряд");

        SYNONYMS_UA.put("змінювати", "трансформувати");
        SYNONYMS_UA.put("знайомий", "звісний");
        SYNONYMS_UA.put("знаряддя", "інструмент");
        SYNONYMS_UA.put("зовнішній", "візуальний");
        SYNONYMS_UA.put("ікона", "образ");
        SYNONYMS_UA.put("індикатор", "покажчик");
        SYNONYMS_UA.put("ініціатива", "почин");
        SYNONYMS_UA.put("іноземець", "чужинець");
        SYNONYMS_UA.put("інтелект", "розум");
        SYNONYMS_UA.put("кавалер", "джентльмен");
        SYNONYMS_UA.put("кабінет", "офіс");
        SYNONYMS_UA.put("капелюх", "шапка");
        SYNONYMS_UA.put("кар'єра", "професійний розвиток");
        SYNONYMS_UA.put("каса", "квиткове вікно");
        SYNONYMS_UA.put("квиток", "талон");
        SYNONYMS_UA.put("кепський", "поганий");
        SYNONYMS_UA.put("команда", "збірна");
        SYNONYMS_UA.put("конкурс", "турнір");
        SYNONYMS_UA.put("копія", "дублікат");

        SYNONYMS_UA.put("користувач", "юзер");
        SYNONYMS_UA.put("коробка", "ящик");
        SYNONYMS_UA.put("крадіжка", "злодійство");
        SYNONYMS_UA.put("край", "округ");
        SYNONYMS_UA.put("критика", "осуд");
        SYNONYMS_UA.put("куля", "шар");
        SYNONYMS_UA.put("лавка", "лава");
        SYNONYMS_UA.put("лак", "покриття");
        SYNONYMS_UA.put("легенда", "переказ");
        SYNONYMS_UA.put("лист", "послання");
        SYNONYMS_UA.put("літак", "аероплан");
        SYNONYMS_UA.put("логіка", "мислення");
        SYNONYMS_UA.put("людство", "людність");
        SYNONYMS_UA.put("люстерко", "дзеркальце");
        SYNONYMS_UA.put("магазин", "крамниця");
        SYNONYMS_UA.put("мама", "ненька");
        SYNONYMS_UA.put("медик", "лікар");
        SYNONYMS_UA.put("мета", "ціль");
        SYNONYMS_UA.put("місяць", "календарний період");

        SYNONYMS_UA.put("місто", "населений пункт");
        SYNONYMS_UA.put("мільйон", "численність");
        SYNONYMS_UA.put("молодь", "юнь");
        SYNONYMS_UA.put("море", "океан");
        SYNONYMS_UA.put("мороз", "крижаний холод");
        SYNONYMS_UA.put("мрія", "прагнення");
        SYNONYMS_UA.put("мудрий", "розсудливий");
        SYNONYMS_UA.put("мужність", "відвага");
        SYNONYMS_UA.put("напій", "питво");
        SYNONYMS_UA.put("народ", "нація");
        SYNONYMS_UA.put("невдача", "програш");
        SYNONYMS_UA.put("нескінченний", "безмежний");
        SYNONYMS_UA.put("нещодавно", "віднедавна");
        SYNONYMS_UA.put("ніж", "лезо");
        SYNONYMS_UA.put("обкладинка", "перепліт");
        SYNONYMS_UA.put("обов'язок", "завдання");
        SYNONYMS_UA.put("обраний", "визначений");
        SYNONYMS_UA.put("обслуговування", "сервіс");
        SYNONYMS_UA.put("обсяг", "об'єм");

        SYNONYMS_UA.put("огляд", "ревізія");
        SYNONYMS_UA.put("одяг", "убрання");
        SYNONYMS_UA.put("озеро", "став");
        SYNONYMS_UA.put("операція", "хірургічне втручання");
        SYNONYMS_UA.put("оплата", "виплата");
        SYNONYMS_UA.put("опонент", "противник");
        SYNONYMS_UA.put("описати", "зобразити");
        SYNONYMS_UA.put("організатор", "ініціатор");
        SYNONYMS_UA.put("оренда", "зйом");
        SYNONYMS_UA.put("оригінал", "первотвор");
        SYNONYMS_UA.put("особа", "індивід");
        SYNONYMS_UA.put("особливий", "винятковий");
        SYNONYMS_UA.put("палац", "замок");
        SYNONYMS_UA.put("парасолька", "зонт");
        SYNONYMS_UA.put("передати", "презентувати");
        SYNONYMS_UA.put("перевірка", "контроль");
        SYNONYMS_UA.put("переживання", "емоції");
        SYNONYMS_UA.put("переконати", "вмовити");
        SYNONYMS_UA.put("перемагати", "здобувати верх");
        SYNONYMS_UA.put("переміщення", "релокація");

        SYNONYMS_UA.put("перерва", "пауза");
        SYNONYMS_UA.put("переважно", "здебільшого");
        SYNONYMS_UA.put("пес", "собака");
        SYNONYMS_UA.put("письменник", "літератор");
        SYNONYMS_UA.put("пізно", "запізніло");
        SYNONYMS_UA.put("підготовка", "готування");
        SYNONYMS_UA.put("підтримувати", "підкріплювати");
        SYNONYMS_UA.put("площа", "майдан");
        SYNONYMS_UA.put("повага", "шанобливість");
        SYNONYMS_UA.put("повернути", "вернути");
        SYNONYMS_UA.put("повністю", "цілком");
        SYNONYMS_UA.put("пожежа", "займання");
        SYNONYMS_UA.put("позичити", "взяти в кредит");
        SYNONYMS_UA.put("покупка", "придбання");
        SYNONYMS_UA.put("полягає", "зводиться");
        SYNONYMS_UA.put("помилка", "хиба");
        SYNONYMS_UA.put("попросити", "звернутися");
        SYNONYMS_UA.put("порівняння", "зіставлення");
        SYNONYMS_UA.put("порука", "застава");
        SYNONYMS_UA.put("порятунок", "спасіння");

        SYNONYMS_UA.put("потреба", "необхідність");
        SYNONYMS_UA.put("почуття", "відчуття");
        SYNONYMS_UA.put("пошкодження", "травма");
        SYNONYMS_UA.put("пояснення", "тлумачення");
        SYNONYMS_UA.put("пошта", "кореспонденція");
        SYNONYMS_UA.put("прайс", "цінник");
        SYNONYMS_UA.put("приклад", "зразок");
        SYNONYMS_UA.put("пристрій", "девайс");
        SYNONYMS_UA.put("прихильник", "сторонник");
        SYNONYMS_UA.put("пришвидшити", "прискорити");
        SYNONYMS_UA.put("провина", "вина");
        SYNONYMS_UA.put("прогрес", "розвиток");
        SYNONYMS_UA.put("продаж", "реалізація");
        SYNONYMS_UA.put("пропозиція", "оферта");
        SYNONYMS_UA.put("простий", "легкий");
        SYNONYMS_UA.put("простір", "територія");
        SYNONYMS_UA.put("процес", "перебіг");
        SYNONYMS_UA.put("прудкий", "швидкий");
        SYNONYMS_UA.put("пункт", "точка");
        SYNONYMS_UA.put("пунктуальний", "точний");

        SYNONYMS_UA.put("радіти", "тішитися");
        SYNONYMS_UA.put("радість", "втіха");
        SYNONYMS_UA.put("рейс", "поїздка");
        SYNONYMS_UA.put("ритм", "темп");
        SYNONYMS_UA.put("рідний", "батьківський");
        SYNONYMS_UA.put("розклад", "графік");


        //-------------------------------RUSSIAN SYNONYMS-----------------------------------
        SYNONYMS_RU.put("клиентов", "покупателей");
        SYNONYMS_RU.put("компаний", "фирм");
        SYNONYMS_RU.put("данных", "информации");
        SYNONYMS_RU.put("рентабельности", "доходности");
        SYNONYMS_RU.put("анализ", "исследование");
        SYNONYMS_RU.put("изучили", "рассмотрели");
        SYNONYMS_RU.put("лояльности", "верности");
        SYNONYMS_RU.put("покупателей", "заказчиков");
        SYNONYMS_RU.put("выручки", "доходов");
        SYNONYMS_RU.put("детальный", "подробный");
        SYNONYMS_RU.put("пособие", "справочник");
        SYNONYMS_RU.put("техник", "методик");
        SYNONYMS_RU.put("ритуалов", "обрядов");
        SYNONYMS_RU.put("внутреннюю", "скрытую");
        SYNONYMS_RU.put("энергию", "силу");
        SYNONYMS_RU.put("управление", "руководство");
        SYNONYMS_RU.put("стихией", "элементом");
        SYNONYMS_RU.put("огня", "пламени");
        SYNONYMS_RU.put("теоретическое", "концептуальное");
        SYNONYMS_RU.put("основание", "базу");

        SYNONYMS_RU.put("техники", "методики");
        SYNONYMS_RU.put("накопление", "сбор");
        SYNONYMS_RU.put("контроля", "управления");
        SYNONYMS_RU.put("практические", "прикладные");
        SYNONYMS_RU.put("упражнения", "задания");
        SYNONYMS_RU.put("фаерболлов", "огненных шаров");
        SYNONYMS_RU.put("методы", "способы");
        SYNONYMS_RU.put("защиты", "обороны");
        SYNONYMS_RU.put("безопасного", "защищённого");
        SYNONYMS_RU.put("использования", "применения");
        SYNONYMS_RU.put("курс", "программа");
        SYNONYMS_RU.put("новичков", "начинающих");
        SYNONYMS_RU.put("опытных", "квалифицированных");
        SYNONYMS_RU.put("практиков", "мастеров");
        SYNONYMS_RU.put("пошаговые", "подробные");
        SYNONYMS_RU.put("инструкции", "указания");
        SYNONYMS_RU.put("советы", "рекомендации");
        SYNONYMS_RU.put("путь", "дорога");
        SYNONYMS_RU.put("изучении", "освоении");

        SYNONYMS_RU.put("магии", "волшебства");
        SYNONYMS_RU.put("энергетика", "энергетическая база");
        SYNONYMS_RU.put("чакры", "энергетические центры");
        SYNONYMS_RU.put("визуализация", "представление");
        SYNONYMS_RU.put("медитация", "погружение");
        SYNONYMS_RU.put("огненная", "пламенная");
        SYNONYMS_RU.put("стихия", "элемент");
        SYNONYMS_RU.put("символика", "знаковая система");
        SYNONYMS_RU.put("нетрадиционными", "альтернативными");
        SYNONYMS_RU.put("практиками", "методами");
        SYNONYMS_RU.put("углубиться", "погрузиться");
        SYNONYMS_RU.put("магический", "волшебный");
        SYNONYMS_RU.put("уникальная", "неповторимая");
        SYNONYMS_RU.put("антология", "сборник");
        SYNONYMS_RU.put("духовные", "религиозные");
        SYNONYMS_RU.put("учения", "доктрины");
        SYNONYMS_RU.put("культур", "традиций");
        SYNONYMS_RU.put("религий", "вероучений");
        SYNONYMS_RU.put("посвященные", "направленные");

        SYNONYMS_RU.put("темам", "вопросам");
        SYNONYMS_RU.put("смерти", "ухода");
        SYNONYMS_RU.put("умирания", "физического угасания");
        SYNONYMS_RU.put("принимать", "воспринимать");
        SYNONYMS_RU.put("жизненного", "экзистенциального");
        SYNONYMS_RU.put("пути", "дороги");
        SYNONYMS_RU.put("предлагает", "предоставляет");
        SYNONYMS_RU.put("размышления", "рассуждения");
        SYNONYMS_RU.put("значение", "смысл");
        SYNONYMS_RU.put("символику", "знаковое содержание");
        SYNONYMS_RU.put("помочь", "способствовать");
        SYNONYMS_RU.put("преодолеть", "побороть");
        SYNONYMS_RU.put("страх", "испуг");
        SYNONYMS_RU.put("тревогу", "беспокойство");
        SYNONYMS_RU.put("неизвестным", "невиданным");
        SYNONYMS_RU.put("экстрасенсорных", "паранормальных");
        SYNONYMS_RU.put("способностей", "возможностей");
        SYNONYMS_RU.put("1930-х", "тридцатых");
        SYNONYMS_RU.put("символов", "знаков");

        SYNONYMS_RU.put("круг", "окружность");
        SYNONYMS_RU.put("крест", "перекрестье");
        SYNONYMS_RU.put("волнистые", "змеевидные");
        SYNONYMS_RU.put("линии", "полосы");
        SYNONYMS_RU.put("квадрат", "четырёхугольник");
        SYNONYMS_RU.put("применяются", "используются");
        SYNONYMS_RU.put("исследованиях", "научных работах");
        SYNONYMS_RU.put("играх", "развлечениях");
        SYNONYMS_RU.put("тренировках", "практикумах");
        SYNONYMS_RU.put("интуиции", "чувства");
        SYNONYMS_RU.put("захватывающая", "интересная");
        SYNONYMS_RU.put("колода", "набор карт");
        SYNONYMS_RU.put("представляет", "демонстрирует");
        SYNONYMS_RU.put("уникальное", "особенное");
        SYNONYMS_RU.put("видение", "восприятие");
        SYNONYMS_RU.put("традиционного", "классического");
        SYNONYMS_RU.put("события", "обстоятельства");
        SYNONYMS_RU.put("классической", "канонической");
        SYNONYMS_RU.put("инструментом", "средством");
        SYNONYMS_RU.put("архетипов", "первичных образов");

        SYNONYMS_RU.put("символов", "значков");
        SYNONYMS_RU.put("ситуаций", "обстоятельств");
        SYNONYMS_RU.put("ракурс", "угол зрения");
        SYNONYMS_RU.put("интерпретации", "толкования");
        SYNONYMS_RU.put("Винтажное", "Ретро");
        SYNONYMS_RU.put("классическая", "традиционная");
        SYNONYMS_RU.put("слегка", "едва");
        SYNONYMS_RU.put("ретро-оформлении", "ретро-стиле");
        SYNONYMS_RU.put("создавая", "вызывая");
        SYNONYMS_RU.put("ощущение", "впечатление");
        SYNONYMS_RU.put("старины", "давности");
        SYNONYMS_RU.put("антиквариата", "антикварности");
        SYNONYMS_RU.put("особенной", "исключительной");
        SYNONYMS_RU.put("атмосферы", "ауры");
        SYNONYMS_RU.put("глубины", "значимости");
        SYNONYMS_RU.put("любителями", "приверженцами");
        SYNONYMS_RU.put("коллекционерами", "собирателями");
        SYNONYMS_RU.put("имитирует", "подражает");
        SYNONYMS_RU.put("найдена", "обнаружена");
        SYNONYMS_RU.put("оригинальном", "аутентичном");
        SYNONYMS_RU.put("легко", "просто");

        SYNONYMS_RU.put("известной", "узнаваемой");
        SYNONYMS_RU.put("размер", "формат");
        SYNONYMS_RU.put("стандартном", "типовом");
        SYNONYMS_RU.put("иногда", "порой");
        SYNONYMS_RU.put("плотной", "толстой");
        SYNONYMS_RU.put("матовой", "неглянцевой");
        SYNONYMS_RU.put("бумаге", "картона");
        SYNONYMS_RU.put("приятными", "комфортными");
        SYNONYMS_RU.put("ощупь", "прикосновение");
        SYNONYMS_RU.put("устойчивыми", "выносливыми");
        SYNONYMS_RU.put("частого", "повторяющегося");
        SYNONYMS_RU.put("новички", "начинающие");
        SYNONYMS_RU.put("винтажное", "ретро");
        SYNONYMS_RU.put("изображения", "иллюстрации");
        SYNONYMS_RU.put("состаренном", "ретро-оформленном");
        SYNONYMS_RU.put("сундуке", "хранилище");
        SYNONYMS_RU.put("ограниченным", "лимитированным");
        SYNONYMS_RU.put("тиражом", "накладом");
        SYNONYMS_RU.put("альтернативные", "другие");
        SYNONYMS_RU.put("буклет", "брошюра");
        SYNONYMS_RU.put("полезным", "нужным");

        SYNONYMS_RU.put("любители", "ценители");
        SYNONYMS_RU.put("классики", "традиционных вариантов");
        SYNONYMS_RU.put("аутентичном", "подлинном");
        SYNONYMS_RU.put("виде", "форме");
        SYNONYMS_RU.put("коллекционеры", "собиратели");
        SYNONYMS_RU.put("ограниченных", "редких");
        SYNONYMS_RU.put("изданиях", "публикациях");
        SYNONYMS_RU.put("свой", "собственный");
        SYNONYMS_RU.put("путь", "маршрут");
    }
}
