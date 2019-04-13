# message-format

### message-format-core 模組

* 定義 MessageFormat 物件
    * 標註 @MessageFormat
    * 繼承 MessageFormatSupport
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

* 定長字串轉物件
```
String dateTimeString = "20190413153000";
SampleDateTime dateTimeObject = MessageFormatHelper.parse(dateTimeString, SampleDateTime.class);
```

* 物件轉定長字串
```
SampleDateTime dateTimeObject = new SampleDateTime();
dateTimeObject.setYear("2019");
dateTimeObject.setDate("0413");
dateTimeObject.setTime("1530");

String dateTimeString = MessageFormatHelper.stringify(sampleObject);
```

### message-format-parser 定長字串解析小工具
* 使用 JavaFX 建立的桌面應用程式
<img src="https://i.imgur.com/gy1uVlN.jpg" width="700">
