package data;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DataGenerator {

    public static Card getValidCard() {
        return new Card("4444 4444 4444 4441", "12", "22", "Card Holder", "123");
    }

    public static Card getDeclinedCard() {
        return new Card("4444 4444 4444 4442", "12", "22", "Card Holder", "123");
    }

    public static Card getFakeCard() {
        return new Card("4444 4444 4444 4449", "12", "22", "Card Holder", "123");
    }

    public static Card getInvalidHolderCard() {
        return new Card("4444 4444 4444 4441", "12", "22", "123456789Йцукенгшщзхъ!\"№;%:?*()123456789Йцукенгшщзхъ!\"№;%:?*()", "123");
    }

    public static Card getInvalidExpDateCard(int months) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, months);
        String date =  new SimpleDateFormat("dd.MM.yy").format(calendar.getTime());
        System.out.println(date);
        String month = new SimpleDateFormat("MM").format(calendar.getTime());
        String year = new SimpleDateFormat("yy").format(calendar.getTime());
        System.out.println(month + " " + year);
        return new Card("4444 4444 4444 4441", month, year, "Card Holder", "123");
    }

}
