taskForCodeVog
==============

Коротке описання реалізації.

1) URL адреси зберігаються у БД SQLite.
база даних заповнюється на початку роботи програми. адреси беруться із файлу ресурсів strings.xml.
і додаються в базу даних. база даних приймає лише унікальні url адреси.
після першого запуску можна закоментувати метод, який додає дані в базу, адже вони там лишаться.

2) з використанням CursorLoader відбувається прив'язка даних із бази, до ListView.

3) також я реалізував SimpleCursorAdapter.ViewBinder, 
у ньому я підвантажував малюнок з інтернету для ImageView кожного елемента списку.
для того, щоб користувач міг бачити зменшені зображення малюнків, ще до того як вони скачаються на девайс.
Щоб це реалізувати, приатачів сторонню бібліотеку, із GitHub.

ось посилання.
https://github.com/koush/UrlImageViewHelper 

4) Розпаралелювання процесу скачування зображень реалізував з використанням 
ExecutorService із пулом у 2 потоки.
Після скачування кожного зображення я вношу зміни в БД. Кожному url протиставляється назва скачаного файлу.
Скачування кожного зображення, відбувається в своєму потоці. 

5) Коли файл завантажено, за допомогою Handler, відсилається
повідомлення, яке обробляється в головному потоці програми.
Якщо файл скачано, у ListView ProgressBar замінюється на зелену "галочку". 

6) При натисканні на елемент списку, через Intent відсилається url в інше Activity програми.
В тому актіві я читаю інтент, і по url знаходжу в базі даних ім'я файлу, який потрібно підвантажити в ImageView.

7) Обробку "гортання" реалізовую унаслідувавшись від класу SimpleOnGestureListener.
в обробнику визначається напрям гортання. (вліво-вправо)
а із бази даних по курсору отримується наступна або попередня позиції
витягується звідти назва файлу.
і змінюється ImageView. 

PS.під час виконання завдання було багато ідей по розширенню функціоналу, 
але я старався притримуватись ТЗ.

я не проводив роботу по тестуванню додатка в різних умовах. 
наприклад, при різних швидкостях інтернет зєднання, на різних девайсах, зокрема різних екранах
не працював над забезпеченням стійкості програми до різного роду помилок.

Деякі проблеми з якими стикнувся під час виконання завдання. 
1) FileExplorer в Eclipse не завжди коректно відображає стан файловової системи на девайсі.
Файловий менеджер "Es прододник" допоміг мені коректно контролювати те, як закачуються файли.

2) Виникла проблема із ListView, так як він не завантажує в пам'ять ті елементи списку, які невидимі на екрані.
Коли я намагався звертатись до невидмих елементів списку, вони мали значення null. Це ускладнило мені реалізацію 
відображення статуса завантаження для кожного зображення, адже коли я хотів проставити "галочку" для прихованих комірок 
то виникало виключення.

Я вирішив цю проблему, хоча можливо підхід не є найелегантнішим.
Після того, як усі файли скачаються, я примусово присвоюю галочки усім коміркам. З логічної точки зору, це коректно, 
так як файли уже скачались. 
Таким чинок, якщо якийсь ProgressBar лишиться видимим не зважаючи на те,що зображення скачалось - це виправиться.
Якщо ж користувач не використовував скролінг під час завантаження, то приховані комірки теж не будуть проініціалізовані. Тому я використав обробник
події скролінгу. І якщо зображення уже завантажені, то під час скролінгу комірки проініціалізуються "галочками". 
Це теж має логічне підгрунтя.
