
package com.openwebinars.todo.tag.service;

import com.openwebinars.todo.tag.model.Tag;
import com.openwebinars.todo.tag.model.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    /*
        Esta función recibe una lista de tags como String,
        y para cada uno de ellos:
        - Comprueba si existe y lo rescata
        - Si no existe, lo inserta y lo devuelve.
     */
    public List<Tag> saveOrGet(List<String> tags) {

        List<Tag> result = new ArrayList<>();

        tags.forEach(tag -> {
            Optional<Tag> val = tagRepository.findByText(tag);
            // Si lo encuentra, se añade a la lista
            // Y si no, se almacena en el repositorio y se añade a la lista
            result.add(val.orElseGet(() -> tagRepository.save(Tag.builder().text(tag).build())));
        });

        return result;

    }

}
