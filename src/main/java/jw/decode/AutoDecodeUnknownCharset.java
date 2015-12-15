package jw.decode;

import org.jsoup.Connection.Response;
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
 * Bellow is HK news site, but in html define, it use BIG5 charset, but very strange can't correct decode by it...
 * I VIOLENCE loop each charset to parse content, when no exception occur then match.
 */
public class AutoDecodeUnknownCharset {

    public static void main(String[] args) {
        AutoDecodeUnknownCharset autoDecode = new AutoDecodeUnknownCharset();

        try {
            Response response = null;
            response = Jsoup.connect("http://www.mpfinance.com/htm/finance/20151118/columnist/en30index.htm").execute();

            System.out.println(autoDecode.decode(response.bodyAsBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Decode.
     *
     * @param bytes
     * @return
     */
    public String decode(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        List<CharsetDecoder> decoders = getCharsetDecoders();
        //get correct CharsetDecoder
        CharsetDecoder currectDecoder = parse(ByteBuffer.wrap(bytes), decoders);

        //use correct CharsetDecoder decode
        CharBuffer cb = null;
        try {
            cb = currectDecoder.decode(ByteBuffer.wrap(bytes));
            while (cb.hasRemaining()) {
                char ch = cb.get();
                sb.append(ch);
            }
        } catch (CharacterCodingException e) {
            e.printStackTrace();
        }
        System.out.println("[correct] " + currectDecoder.charset());

        return sb.toString();
    }

    /**
     * Initial CharsetDecoder List.
     *
     * @return
     */
    public List<CharsetDecoder> getCharsetDecoders() {
        List<CharsetDecoder> decoders = new ArrayList<CharsetDecoder>();
        Map<String, Charset> map = Charset.availableCharsets();
        Iterator<Map.Entry<String, Charset>> ite = map.entrySet().iterator();
        while (ite.hasNext()) {
            Map.Entry<String, Charset> entry = ite.next();
            decoders.add(entry.getValue().newDecoder());
        }

        return decoders;
    }

    /**
     * Force parse each charset when match correct.
     *
     * @param bytes
     * @param decoders
     * @return
     */
    public CharsetDecoder parse(ByteBuffer byteBuffer, List<CharsetDecoder> decoders) {
        for (int i = 0; i < decoders.size(); i++) {
            try {
                decoders.get(i).decode(byteBuffer);
                return decoders.get(i);
            } catch (CharacterCodingException e) {
                System.out.println(decoders.get(i).charset() + " not correct..., try next~");
            }
        }

        return null;
    }
}
