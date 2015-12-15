package jw;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        try {
            CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();

            Connection.Response response = null;

            response = Jsoup.connect("http://www.mpfinance.com/htm/finance/20151118/columnist/en30index.htm").execute();

            System.out.println(decode(response.bodyAsBytes()));
//            System.out.println(decode("I am batman~~~".getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String decode(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        List<CharsetDecoder> decoders = new ArrayList<CharsetDecoder>();
        Map<String, Charset> map = Charset.availableCharsets();
        Iterator<Map.Entry<String, Charset>> ite = map.entrySet().iterator();
        while (ite.hasNext()) {
            Map.Entry<String, Charset> entry = ite.next();
            decoders.add(entry.getValue().newDecoder());
        }

        //
        CharsetDecoder currectDecoder = null;
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        for (int i = 0; i < decoders.size(); i++) {
            try {
                decoders.get(i).decode(bb);
                currectDecoder = decoders.get(i);
                break;
            } catch (CharacterCodingException e) {
                System.out.println(decoders.get(i).charset()+" not correct..., try next~");
            }
        }
        CharBuffer cb = null;
        try {
            cb = currectDecoder.decode(ByteBuffer.wrap(bytes));
            System.out.println(bb.array().length);
            while (cb.hasRemaining()) {
                char ch = cb.get();
                sb.append(ch);
            }
        } catch (CharacterCodingException e) {
            e.printStackTrace();
        }
        System.out.println("[currect] "+currectDecoder.charset());
System.out.println(sb.toString());
        return sb.toString();
    }
}
