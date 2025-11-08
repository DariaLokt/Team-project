package com.team.recommendations.service;

import com.team.recommendations.model.dynamic.DynamicProduct;
import com.team.recommendations.model.dynamic.DynamicRule;
import com.team.recommendations.repository.DynamicProductRepository;
import com.team.recommendations.repository.DynamicRuleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    private final DynamicProductRepository dynamicProductRepository;
    private final DynamicRuleRepository dynamicRuleRepository;

    public DataInitializer(DynamicProductRepository dynamicProductRepository, DynamicRuleRepository dynamicRuleRepository) {
        this.dynamicProductRepository = dynamicProductRepository;
        this.dynamicRuleRepository = dynamicRuleRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (dynamicProductRepository.count() == 0) {
            initData();
        }
    }

    private void initData() {
        /**
         * Create initial products
         */
        DynamicProduct invest500Product = createDynamicProduct("Invest 500",UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a"),"Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) от нашего банка! Воспользуйтесь налоговыми льготами и начните инвестировать с умом. Пополните счет до конца года и получите выгоду в виде вычета на взнос в следующем налоговом периоде. Не упустите возможность разнообразить свой портфель, снизить риски и следить за актуальными рыночными тенденциями. Откройте ИИС сегодня и станьте ближе к финансовой независимости!");
        DynamicProduct topSavingProduct = createDynamicProduct("Top Saving",UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925"),"Откройте свою собственную «Копилку» с нашим банком! «Копилка» — это уникальный банковский инструмент, который поможет вам легко и удобно накапливать деньги на важные цели. Больше никаких забытых чеков и потерянных квитанций — всё под контролем!\n" +
                "\n" +
                "Преимущества «Копилки»:\n" +
                "\n" +
                "Накопление средств на конкретные цели. Установите лимит и срок накопления, и банк будет автоматически переводить определенную сумму на ваш счет.\n" +
                "\n" +
                "Прозрачность и контроль. Отслеживайте свои доходы и расходы, контролируйте процесс накопления и корректируйте стратегию при необходимости.\n" +
                "\n" +
                "Безопасность и надежность. Ваши средства находятся под защитой банка, а доступ к ним возможен только через мобильное приложение или интернет-банкинг.\n" +
                "\n" +
                "Начните использовать «Копилку» уже сегодня и станьте ближе к своим финансовым целям!");
        DynamicProduct simpleCreditProduct = createDynamicProduct("Простой кредит",UUID.fromString("ab138afb-f3ba-4a93-b74f-0fcee86d447f"),                    "Откройте мир выгодных кредитов с нами!\n" +
                "\n" +
                "Ищете способ быстро и без лишних хлопот получить нужную сумму? Тогда наш выгодный кредит — именно то, что вам нужно! Мы предлагаем низкие процентные ставки, гибкие условия и индивидуальный подход к каждому клиенту.\n" +
                "\n" +
                "Почему выбирают нас:\n" +
                "\n" +
                "Быстрое рассмотрение заявки. Мы ценим ваше время, поэтому процесс рассмотрения заявки занимает всего несколько часов.\n" +
                "\n" +
                "Удобное оформление. Подать заявку на кредит можно онлайн на нашем сайте или в мобильном приложении.\n" +
                "\n" +
                "Широкий выбор кредитных продуктов. Мы предлагаем кредиты на различные цели: покупку недвижимости, автомобиля, образование, лечение и многое другое.\n" +
                "\n" +
                "Не упустите возможность воспользоваться выгодными условиями кредитования от нашей компании!");

        /**
         * Save initial products to dynamic products repository
         */
        Collection<DynamicProduct> initialProducts = dynamicProductRepository.saveAll(List.of(topSavingProduct,simpleCreditProduct,invest500Product));

        /**
         * Create initial rules
         */
        DynamicRule invest500Rule1 = createDynamicRule("USER_OF",List.of("DEBIT"),false,invest500Product);
        DynamicRule invest500Rule2 = createDynamicRule("USER_OF",List.of("INVEST"),true,invest500Product);
        DynamicRule invest500Rule3 = createDynamicRule("TRANSACTION_SUM_COMPARE",List.of("SAVING","DEPOSIT",">","1000"),false,invest500Product);

        DynamicRule topSavingRule1 = createDynamicRule("USER_OF",List.of("DEBIT"),false,topSavingProduct);
        DynamicRule topSavingRule2_1 = createDynamicRule("TRANSACTION_SUM_COMPARE",List.of("DEBIT","DEPOSIT",">=","50000"),false,topSavingProduct);
        DynamicRule topSavingRule2_2 = createDynamicRule("TRANSACTION_SUM_COMPARE",List.of("SAVING","DEPOSIT",">=","50000"),false,topSavingProduct);
        DynamicRule topSavingRule3 = createDynamicRule("TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW",List.of("DEBIT",">"),false,topSavingProduct);

        DynamicRule simpleCreditRule1 = createDynamicRule("USER_OF",List.of("CREDIT"),true,simpleCreditProduct);
        DynamicRule simpleCreditRule2 = createDynamicRule("TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW",List.of("DEBIT",">"),false,simpleCreditProduct);
        DynamicRule simpleCreditRule3 = createDynamicRule("TRANSACTION_SUM_COMPARE",List.of("DEBIT","DEPOSIT",">","100000"),false,simpleCreditProduct);

        /**
         * Save rules to dynamic rules repository
         */
        dynamicRuleRepository.saveAll(List.of(invest500Rule1,invest500Rule2,invest500Rule3,topSavingRule1,topSavingRule2_1,topSavingRule2_2,topSavingRule3,simpleCreditRule1,simpleCreditRule2,simpleCreditRule3));
    }

    private DynamicProduct createDynamicProduct(String name, UUID id, String text) {
        DynamicProduct dynamicProduct = new DynamicProduct();
        dynamicProduct.setProductId(id);
        dynamicProduct.setProductName(name);
        dynamicProduct.setProductText(text);
        return dynamicProduct;
    }

    private DynamicRule createDynamicRule(String query, Collection<String> arguments, Boolean negate, DynamicProduct dynamicProduct) {
        DynamicRule dynamicRule = new DynamicRule();
        dynamicRule.setQuery(query);
        dynamicRule.setArguments(arguments);
        dynamicRule.setNegate(negate);
        dynamicRule.setProduct(dynamicProduct);
        return dynamicRule;
    }
}
