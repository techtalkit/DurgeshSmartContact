package com.example.smartcontactmanager.dao;

import com.example.smartcontactmanager.entities.MyOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyOrderRepository extends JpaRepository<MyOrder,Long> {
    public MyOrder findByOrderId(String orderId);
}
