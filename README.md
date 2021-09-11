# Sleep that speeds up the test

This repository shows an interesting case: when sleep is added to the test, the test gets faster. 

## How to run
> mvn test

## The slow test
See in output:
```
INFO DeclarationFormTest - get webdriver: 0 ms.
INFO DeclarationFormTest - find element: 12 ms.
INFO DeclarationFormTest - first click: 1076 ms.
INFO DeclarationFormTest - First click failed
org.openqa.selenium.ElementClickInterceptedException: element click intercepted: Element is not clickable at point (416, 1179)
(Session info: chrome=93.0.4577.63)
...
```

As you see, the "first click" takes more than a second and fails. 

## The faster test
To make this test faster, uncomment the `sleep(300)` in `DeclarationFormTest.java`

This time "first click" takes only 100ms:

```
INFO DeclarationFormTest - get webdriver: 0 ms.
INFO DeclarationFormTest - find element: 13 ms.
INFO DeclarationFormTest - first click: 105 ms.
```