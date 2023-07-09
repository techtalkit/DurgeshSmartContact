package com.example.smartcontactmanager.entities;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name="orders")
public class MyOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long myOrderId;
    private String orderId;
    private String amount;
    private String receipt;
    private String status;
    //A particular user can do may orders
    @ManyToOne
    private User user;
    private String paymentId;

}
