package com.heartfoilo.demo.domain.stock.service;

import com.heartfoilo.demo.domain.invest.entity.Order;
import com.heartfoilo.demo.domain.invest.repository.OrderRepository;
import com.heartfoilo.demo.domain.stock.constant.ErrorMessage;
import com.heartfoilo.demo.domain.stock.dto.responseDto.OrderHistoryResponseDto;
import com.heartfoilo.demo.domain.stock.dto.responseDto.StockSearchResponseDto;
import com.heartfoilo.demo.domain.stock.entity.Stock;
import com.heartfoilo.demo.domain.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final StockRepository stockRepository;

    @Override
    public List<OrderHistoryResponseDto> getOrderHistory(Long userId, Long stockId) {
        List<Order> orders = orderRepository.findByStockIdAndUserId(stockId, userId);
        //FIXME: 상태코드 변경 필요
        if (orders.isEmpty()) {
            throw new IllegalArgumentException(ErrorMessage.STOCK_SEARCH_NOT_FOUND);
        }
        return orders.stream()
                .map(order -> new OrderHistoryResponseDto(
                        order.getId(),
                        order.getOrderCategory(),
                        order.getOrderDate(),
                        Math.toIntExact(order.getOrderAmount()),
                        order.getOrderPrice()
                )).collect(Collectors.toList());
    }

    @Override
    public List<StockSearchResponseDto> getStockSearch(String keyword) {
        List<Stock> stocks = stockRepository.findByNameContainingOrSymbolContaining(keyword, keyword);
        if(stocks.isEmpty()) {
            throw new IllegalArgumentException(ErrorMessage.ORDER_HISTORY_NOT_FOUND);
        }
        return stocks.stream()
                .map(stock -> new StockSearchResponseDto(
                        stock.getId(),
                        stock.getName()
                )).collect(Collectors.toList());
    }
}
