package com.dtw.assignment.config;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import feign.gson.GsonDecoder;
import feign.jackson.JacksonDecoder;

public class FeignDecoder implements Decoder {

	@Override
	public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {

		GsonDecoder gsonDecoder =  new GsonDecoder();
		JacksonDecoder jacksonDecoder = new JacksonDecoder();
		
		if(type.getTypeName().startsWith("org.springframework.http.ResponseEntity")) {
			if(type.getTypeName().equals("org.springframework.http.ResponseEntity<org.springframework.core.io.ByteArrayResource>")) {
				
				MultiValueMap<String, String> multiMap = new LinkedMultiValueMap<>();
				Map<String, List<String>> map = response.headers().entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().stream().collect(Collectors.toList())));
				for(Entry<String, List<String>> entry : map.entrySet()) {
					multiMap.put(entry.getKey(), entry.getValue());
				}
				
				return ResponseEntity.
						status(response.status()).
						headers(new HttpHeaders(multiMap)).
						body(new ByteArrayResource(response.body().asInputStream().readAllBytes()));
			}
			return jacksonDecoder.decode(response, type);
		}
		else {
			return gsonDecoder.decode(response, type);
		}
	}
}
