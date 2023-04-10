# Learn

## Inbox

### Learn
- tomcat 동작원리 : https://exhibitlove.tistory.com/312
    - tomcat 이란, 역할 책임
    - servlet : https://coding-factory.tistory.com/742
- spring boot
    - spring vs springboot
    - 동작원리 : dispather servlet.
        - front controller pattern : controller 앞단에서 공통 로직 처리, MVC
        - https://yoonbing9.tistory.com/79 : 이전엔 RequestDispather 계속 만듬(for forward)
    - filter & interceptor
- architecture
    - software architecture pattern : https://towardsdatascience.com/10-common-software-architectural-patterns-in-a-nutshell-a0b47a1e9013
        - layered
        - hexagonal

- test
    - test란?
        - BDD~~
    - junit
    - spock

### 추가
- modelmapper vs mapstruct
- ModelMapper나 Orika에서 메모리를 많이 활용하고 성능이 저하되는 것은 runtime 시점에 reflection을 통해 맵핑을 하기 떄문에 맵핑 객체의 사이즈가 커질수록 선형적으로 메모리를 많이 사용하게 될 것이며 성능도 저하될 수 밖에 없다.(비효율)
- MapStruct의 성능이 우수한 이유는 Lombok과 같이 annotation processor를 통해서 compile 시점에 객체간 맵핑이 이루어지기 떄문에 매우 월등한 성능을 보일 수 있다.

import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

public interface GenericMapper <D, E> {
D toDto(E e);
E toEntity(D d);

    List<D> toDtoList(List<E> entityList);
    List<E> toEntityList(List<D> dtoList);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(D dto, @MappingTarget E entity);
}
