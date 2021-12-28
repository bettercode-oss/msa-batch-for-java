package kr.bettercode.msabatchforjava.repository;

import kr.bettercode.msabatchforjava.model.example.Example;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExampleRepository {

  Example getExample();
}
