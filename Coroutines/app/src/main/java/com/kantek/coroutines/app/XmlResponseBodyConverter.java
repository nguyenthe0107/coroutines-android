package com.kantek.coroutines.app;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

final class XmlResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Class<T> cls;

    XmlResponseBodyConverter(Class<T> cls) {
        this.cls = cls;

    }

    @Override
    public T convert(ResponseBody value) throws IOException {
//        try {
//            XMLSerializer xmlSerializer = new XMLSerializer();
//            JSON json = xmlSerializer.read( xml );
//            String xml = value.string();
//            Field[] fields = this.cls.getDeclaredFields();
//            T object = cls.newInstance();
//            for (Field field : fields) {
//                Type type = field.getType();
//                field.set(object, );
//            }
//        } catch (RuntimeException | IOException e) {
//            throw e;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        } finally {
//            value.close();
//        }
        throw new IOException("");
    }
}
