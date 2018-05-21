Порядок установки приложения:
1. Скачать и установить CAPICOM

On 32-bit platform:

Download CAPICOM – http://www.microsoft.com/en-us/download/details.aspx?id=25281
Open an administrative command prompt
Execute regsvr32.exe capicom.dll

On 64-bit platform:

Download CAPICOM – http://www.microsoft.com/en-us/download/details.aspx?id=25281
Open an administrative command prompt
cd to "C:\Program Files (x86)\Microsoft CAPICOM 2.1.0.2 SDK\Lib\X86"
copy CAPICOM.DLL %windir%\syswow64

%windir%\syswow64\regsvr32.exe %windir%\syswow64\capicom.dll


2. Скачать и установить Java 8
https://java.com/ru/download/

3. Запустить приложение из его директирии, убедится что оно запустилось
java -jar ldsign-1.0.jar

4. Попробовать выполнить http запросы согласно ТЗ.
Примеры:
POST:
http://localhost:13578/getcerts
REQUEST BODY:
{"provider":"CAPICOM",
  "storelocation":2}

Результат:

