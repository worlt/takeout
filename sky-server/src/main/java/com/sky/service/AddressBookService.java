package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

/**
 * @Author worlt
 * @Date 2025/4/28 下午4:53
 */
public interface AddressBookService {

    List<AddressBook> list(AddressBook addressBook);

    void save(AddressBook addressBook);

    AddressBook getById(Long id);

    void update(AddressBook addressBook);

    void setDefault(AddressBook addressBook);

    void deleteById(Long id);
}
