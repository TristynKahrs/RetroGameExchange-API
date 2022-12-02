package com.tkahrs.retroexchange.Repositories;

import com.tkahrs.retroexchange.Models.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeJpaRepository extends JpaRepository<Trade, Integer> {

}
