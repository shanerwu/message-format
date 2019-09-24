# message-format

### message-format-core Module

* Define MessageFormat Object
    * annotated @MessageFormat
    * extends MessageFormatSupport
```
@Getter
@Setter
public class SampleDateTime extends MessageFormatSupport {

    @MessageFormat(length = 4)
    private String year;

    @MessageFormat(length = 4)
    private String date;

    @MessageFormat(length = 4)
    private String time;

    @MessageFormat(length = 2)
    private int listCount;

    @MessageFormat(length = 12, reference = "listCount")
    private List<SampleDateTime> list;

}
```

* Parse Fixed-Length Strings to Object fields 定長字串轉物件

   `MessageFormatHelper.parse(String text, Class<T> clazz)`
   ```
   String dateTimeString = "20190413153000";
   SampleDateTime dateTimeObject = MessageFormatHelper.parse(dateTimeString, SampleDateTime.class);

   // dateTimeObject -> { year: "2019", date: "0413", time: "1530", listCount: 0, list: null }
   ```

* Stringify Object fields to Fixed-Length Strings 物件轉定長字串

   `MessageFormatHelper.stringify(MessageFormatSupport format)`
   ```
   SampleDateTime dateTimeObject = new SampleDateTime();
   dateTimeObject.setYear("2019");
   dateTimeObject.setDate("0413");
   dateTimeObject.setTime("1530");

   String dateTimeString = MessageFormatHelper.stringify(sampleObject);
   // dateTimeString -> "20190413153000"
   ```

### message-format-parser Module 定長字串解析小工具
* A simple GUI tool, Builded with JavaFX to parse fixed-length strings then show parsed message beside.
<img src="https://i.imgur.com/S1E0GBz.jpg" width="700">
