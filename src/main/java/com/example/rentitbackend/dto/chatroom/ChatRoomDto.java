package com.example.rentitbackend.dto.chatroom;

import com.example.rentitbackend.dto.member.MemberDto;
import com.example.rentitbackend.dto.message.MessageDto;
import com.example.rentitbackend.dto.product.ProductDto;
import com.example.rentitbackend.entity.ChatRoom;
import com.example.rentitbackend.entity.Member;
import com.example.rentitbackend.entity.Product;
import com.example.rentitbackend.entity.ProductImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ChatRoomDto {
    @Getter
    @AllArgsConstructor
    public static class Request {
        private UUID sellerId;
        private UUID buyerId;
        private Long productId;

        public ChatRoom toChatRoom() {
            return ChatRoom.builder()
                    .seller(Member.builder().id(sellerId).build())
                    .buyer(Member.builder().id(buyerId).build())
                    .product(Product.builder().id(productId).build())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private MemberDto.Response seller;
        private MemberDto.Response buyer;
        private ProductDto.Summary product;

        public static Response of(ChatRoom chatRoom) {
            MemberDto.Response seller = null, buyer = null;
            ProductDto.Summary product = null;
            if(chatRoom.getSeller() != null) seller = MemberDto.Response.of(chatRoom.getSeller());
            if(chatRoom.getBuyer() != null) buyer = MemberDto.Response.of(chatRoom.getBuyer());
            if(chatRoom.getProduct() != null) {
                product = ProductDto.Summary.builder()
                        .id(chatRoom.getProduct().getId())
                        .title(chatRoom.getProduct().getTitle())
                        .price(chatRoom.getProduct().getPrice())
                        .duration(chatRoom.getProduct().getDuration())
                        .sellerName(chatRoom.getSeller().getNickname())
                        .productImages(chatRoom.getProduct().getImages().stream().map(ProductImage::getStoreFileName).collect(Collectors.toList()))
                        .build();
            }
            return Response.builder()
                    .id(chatRoom.getId())
                    .seller(seller)
                    .buyer(buyer)
                    .product(product)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Detail {
        private Long id;
        private MemberDto.Response seller;
        private MemberDto.Response buyer;
        private List<MessageDto.Response> sortedMessages;

        public static Detail of(ChatRoom chatRoom) {
            List<MessageDto.Response> sortedMessages = chatRoom.getMessageList().stream()
                    .sorted(Comparator.comparing(message -> message.getSendTime(), Comparator.reverseOrder()))
                    .map(MessageDto.Response::of)
                    .collect(Collectors.toList());

            return Detail.builder()
                    .id(chatRoom.getId())
                    .seller(MemberDto.Response.of(chatRoom.getSeller()))
                    .buyer(MemberDto.Response.of(chatRoom.getBuyer()))
                    .sortedMessages(sortedMessages)
                    .build();
        }
    }
//    public static class Detail {
//        private Long id;
//        private MemberDto.Response seller;
//        private MemberDto.Response buyer;
//        private List<MessageDto.Response> messages;
//
//        public static Detail of(ChatRoom chatRoom) {
//            return Detail.builder()
//                    .id(chatRoom.getId())
//                    .seller(MemberDto.Response.of(chatRoom.getSeller()))
//                    .buyer(MemberDto.Response.of(chatRoom.getBuyer()))
//                    .messages(chatRoom.getMessageList().stream().map(MessageDto.Response::of).collect(Collectors.toList()))
//                    .build();
//        }
//    }
}
