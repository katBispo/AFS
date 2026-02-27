
# Telecom Model (Spring Boot / JPA)

Este pacote contém **todas as entidades, enums e repositórios** para o domínio de Telecom/Monitoramento com correlações (IP↔VLAN, Rotas↔Interfaces, etc.).

## Requisitos
- Java 17+
- Maven 3.8+

## Como iniciar (com H2 memória)
```bash
mvn -q -DskipTests spring-boot:run
```

## Observações
- `ddl-auto: update` apenas para dev. Em prod, utilize migrações (Flyway/Liquibase).
- Ajuste dependências de banco conforme necessário (PostgreSQL/MySQL).
