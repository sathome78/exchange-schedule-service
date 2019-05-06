package me.exrates.scheduleservice.repositories.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.scheduleservice.models.dto.ExternalWalletBalancesDto;
import me.exrates.scheduleservice.models.dto.InternalWalletBalancesDto;
import me.exrates.scheduleservice.models.enums.UserRole;
import me.exrates.scheduleservice.repositories.WalletDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2(topic = "Dao_layer_log")
@Repository
public class WalletDaoImpl implements WalletDao {

    private final NamedParameterJdbcOperations masterJdbcTemplate;
    private final NamedParameterJdbcOperations slaveJdbcTemplate;

    @Autowired
    public WalletDaoImpl(@Qualifier(value = "masterTemplate") NamedParameterJdbcOperations masterJdbcTemplate,
                         @Qualifier(value = "slaveTemplate") NamedParameterJdbcOperations slaveJdbcTemplate) {
        this.masterJdbcTemplate = masterJdbcTemplate;
        this.slaveJdbcTemplate = slaveJdbcTemplate;
    }

    @Override
    public List<ExternalWalletBalancesDto> getExternalMainWalletBalances() {
        String sql = "SELECT cur.id as currency_id , " +
                "cur.name AS currency_name, " +
                "cewb.usd_rate, " +
                "cewb.btc_rate, " +
                "if(cur.hidden or cewb.main_balance is null, 0, cewb.main_balance) as main_balance,  " +
                "if(cur.hidden or cewb.reserved_balance is null , 0, cewb.reserved_balance) as reserved_balance, " +
                "if(cur.hidden or cewb.total_balance is null, 0, cewb.total_balance) as total_balance, " +
                "if(cur.hidden or cewb.total_balance_usd is null, 0, cewb.total_balance_usd) as total_balance_usd, " +
                "if(cur.hidden or cewb.total_balance_btc is null, 0, cewb.total_balance_btc) as total_balance_btc, " +
                "cewb.last_updated_at, " +
                "cewb.sign_of_certainty " +
                " FROM COMPANY_EXTERNAL_WALLET_BALANCES cewb" +
                " RIGHT JOIN CURRENCY cur on cewb.currency_id = cur.id" +
                " ORDER BY currency_id";

        return slaveJdbcTemplate.query(sql, (rs, row) -> ExternalWalletBalancesDto.builder()
                .currencyId(rs.getInt("currency_id"))
                .currencyName(rs.getString("currency_name"))
                .usdRate(rs.getBigDecimal("usd_rate"))
                .btcRate(rs.getBigDecimal("btc_rate"))
                .mainBalance(rs.getBigDecimal("main_balance"))
                .reservedBalance(rs.getBigDecimal("reserved_balance"))
                .totalBalance(rs.getBigDecimal("total_balance"))
                .totalBalanceUSD(rs.getBigDecimal("total_balance_usd"))
                .totalBalanceBTC(rs.getBigDecimal("total_balance_btc"))
                .lastUpdatedDate(rs.getTimestamp("last_updated_at").toLocalDateTime())
                .signOfCertainty(rs.getBoolean("sign_of_certainty"))
                .build());
    }

    @Override
    public List<InternalWalletBalancesDto> getInternalWalletBalances() {
        final String sql = "SELECT iwb.currency_id, " +
                "cur.name AS currency_name, " +
                "iwb.role_id, " +
                "ur.name AS role_name, " +
                "iwb.usd_rate, " +
                "iwb.btc_rate, " +
                "iwb.total_balance, " +
                "iwb.total_balance_usd, " +
                "iwb.total_balance_btc, " +
                "iwb.last_updated_at" +
                " FROM INTERNAL_WALLET_BALANCES iwb" +
                " JOIN CURRENCY cur ON (cur.id = iwb.currency_id AND cur.hidden = 0)" +
                " JOIN USER_ROLE ur ON ur.id = iwb.role_id" +
                " ORDER BY iwb.currency_id, iwb.role_id";

        return slaveJdbcTemplate.query(sql, (rs, row) -> InternalWalletBalancesDto.builder()
                .currencyId(rs.getInt("currency_id"))
                .currencyName(rs.getString("currency_name"))
                .roleId(rs.getInt("role_id"))
                .roleName(UserRole.valueOf(rs.getString("role_name")))
                .usdRate(rs.getBigDecimal("usd_rate"))
                .btcRate(rs.getBigDecimal("btc_rate"))
                .totalBalance(rs.getBigDecimal("total_balance"))
                .totalBalanceUSD(rs.getBigDecimal("total_balance_usd"))
                .totalBalanceBTC(rs.getBigDecimal("total_balance_btc"))
                .lastUpdatedDate(rs.getTimestamp("last_updated_at").toLocalDateTime())
                .build());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void updateExternalMainWalletBalances(ExternalWalletBalancesDto externalWalletBalancesDto) {
        final String sql = "UPDATE COMPANY_EXTERNAL_WALLET_BALANCES cewb" +
                " SET cewb.usd_rate = :usd_rate, cewb.btc_rate = :btc_rate, " +
                "cewb.main_balance = :main_balance, " +
                "cewb.total_balance = cewb.main_balance + cewb.reserved_balance, " +
                "cewb.total_balance_usd = cewb.total_balance * cewb.usd_rate, " +
                "cewb.total_balance_btc = cewb.total_balance * cewb.btc_rate, " +
                "cewb.last_updated_at = IFNULL(:last_updated_at, cewb.last_updated_at)" +
                " WHERE cewb.currency_id = :currency_id";

        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("currency_id", externalWalletBalancesDto.getCurrencyId());
                put("usd_rate", externalWalletBalancesDto.getUsdRate());
                put("btc_rate", externalWalletBalancesDto.getBtcRate());
                put("main_balance", externalWalletBalancesDto.getMainBalance());
                put("last_updated_at", externalWalletBalancesDto.getLastUpdatedDate());
            }
        };

        masterJdbcTemplate.update(sql, params);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void updateInternalWalletBalances(InternalWalletBalancesDto internalWalletBalancesDto) {
        final String sql = "UPDATE INTERNAL_WALLET_BALANCES iwb" +
                " SET iwb.usd_rate = :usd_rate, iwb.btc_rate = :btc_rate, " +
                "iwb.total_balance = IFNULL(:total_balance, 0), " +
                "iwb.total_balance_usd = iwb.total_balance * iwb.usd_rate, " +
                "iwb.total_balance_btc = iwb.total_balance * iwb.btc_rate, " +
                "iwb.last_updated_at = CURRENT_TIMESTAMP" +
                " WHERE iwb.currency_id = :currency_id AND iwb.role_id = :role_id";

        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("currency_id", internalWalletBalancesDto.getCurrencyId());
                put("role_id", internalWalletBalancesDto.getRoleId());
                put("usd_rate", internalWalletBalancesDto.getUsdRate());
                put("btc_rate", internalWalletBalancesDto.getBtcRate());
                put("total_balance", internalWalletBalancesDto.getTotalBalance());
            }
        };
        masterJdbcTemplate.update(sql, params);
    }

    @Override
    public List<InternalWalletBalancesDto> getWalletBalances() {
        final String sql = "SELECT cur.id AS currency_id, " +
                "cur.name AS currency_name, " +
                "ur.id AS role_id, " +
                "ur.name AS role_name, " +
                "SUM(w.active_balance + w.reserved_balance) AS total_balance" +
                " FROM WALLET w" +
                " JOIN CURRENCY cur ON cur.id = w.currency_id AND cur.hidden = 0" +
                " JOIN USER u ON u.id = w.user_id" +
                " JOIN USER_ROLE ur ON ur.id = u.roleid" +
                " GROUP BY cur.id, ur.id" +
                " ORDER BY cur.id, ur.id";

        return slaveJdbcTemplate.query(sql, (rs, row) -> InternalWalletBalancesDto.builder()
                .currencyId(rs.getInt("currency_id"))
                .currencyName(rs.getString("currency_name"))
                .roleId(rs.getInt("role_id"))
                .roleName(UserRole.valueOf(rs.getString("role_name")))
                .totalBalance(rs.getBigDecimal("total_balance"))
                .build());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void updateExternalReservedWalletBalances(int currencyId, String walletAddress, BigDecimal balance, LocalDateTime lastReservedBalanceUpdate) {
        String sql = "UPDATE birzha.COMPANY_WALLET_EXTERNAL_RESERVED_ADDRESS cwera" +
                " SET cwera.balance = :balance" +
                " WHERE cwera.currency_id = :currency_id" +
                " AND cwera.wallet_address = :wallet_address";

        Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("currency_id", currencyId);
                put("wallet_address", walletAddress);
                put("balance", balance);
            }
        };
        masterJdbcTemplate.update(sql, params);

        sql = "UPDATE COMPANY_EXTERNAL_WALLET_BALANCES cewb" +
                " SET cewb.reserved_balance = IFNULL((SELECT SUM(cwera.balance) FROM COMPANY_WALLET_EXTERNAL_RESERVED_ADDRESS cwera WHERE cwera.currency_id = :currency_id GROUP BY cwera.currency_id), 0), " +
                "cewb.total_balance = cewb.main_balance + cewb.reserved_balance, " +
                "cewb.total_balance_usd = cewb.total_balance * cewb.usd_rate, " +
                "cewb.total_balance_btc = cewb.total_balance * cewb.btc_rate, " +
                "cewb.last_updated_at = IFNULL(:last_updated_at, cewb.last_updated_at)" +
                " WHERE cewb.currency_id = :currency_id";

        params = new HashMap<String, Object>() {
            {
                put("currency_id", currencyId);
                put("last_updated_at", lastReservedBalanceUpdate);
            }
        };
        masterJdbcTemplate.update(sql, params);
    }
}