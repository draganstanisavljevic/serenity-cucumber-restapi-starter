package com.orgname.qa.utils.helper;

import com.github.javafaker.Faker;
import com.orgname.qa.model.petstore.*;
import org.threeten.bp.OffsetDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PetApiDataHelper {

    Faker faker = new Faker();
    private static final String PHOTO_URL = "https://flowers.ua/images/Flowers/2334.jpg";

    public Category setCategory(){
        Category category = new Category();
        category.setId(faker.random().nextInt(1, 10000).longValue());
        category.setName("TestAutomationCategory");
        return category;
    }

    public Order setOrder(){
        Order order = new Order();
        order.setId(faker.random().nextInt(1, 10000).longValue());
        order.setStatus(Order.StatusEnum.DELIVERED);
        order.setQuantity(faker.random().nextInt(1, 30));
        order.setComplete(true);
        order.setPetId(faker.random().nextLong());
        order.setShipDate("2021-12-28T14:16:04.124+0000");
        return order;
    }

    public Pet setPet(){
        Pet pet = new Pet();
        pet.setCategory(setCategory());
        pet.setName(faker.name().firstName());
        pet.setStatus(Pet.StatusEnum.AVAILABLE);
        pet.setId(faker.random().nextInt(1, 10000).longValue());
        List<String> photoUrls = new ArrayList<>();
        photoUrls.add(PHOTO_URL);
        pet.setPhotoUrls(photoUrls);
        List<Tag> tags = new ArrayList<>();
        tags.add(setTag());
        pet.setTags(tags);
        return pet;
    }

    public Tag setTag(){
        Tag tag = new Tag();
        tag.setId(faker.random().nextInt(1, 10000).longValue());
        tag.setName(faker.funnyName().name());
        return tag;
    }

    public User setUser(){
        User user = new User();
        String email = faker.bothify("????##@gmail.com");
        user.setEmail(email);
        user.setUsername(faker.name().username());
        user.setUserStatus(faker.random().nextInt(1, 20));
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());
        user.setId(faker.random().nextInt(1, 10000).longValue());
        user.setPhone(faker.phoneNumber().phoneNumber());
        user.setPassword(faker.funnyName().name());
        return user;
    }
}
