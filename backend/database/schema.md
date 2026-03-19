# Schema Summary & Suggestions

This document summarizes the current schema (`02_tables.sql`) and recommended edits made/completed.

## Changes applied
- Added `created_at` and `updated_at` columns to `stock_batch`, `purchase_item`, and `sale_item` for consistency.
- Added `ON DELETE CASCADE` to foreign keys from `purchase_item.purchase_id -> purchase(id)` and `sale_item.sale_id -> sale(id)` to ensure child items are removed when parent is deleted.
- Added a list of recommended indexes to be created in `03_indexes.sql` (see section `Indexes to add`).
- Created a PlantUML ER diagram at `database/er_diagram.puml`.

## Indexes to add (put these in `03_indexes.sql`)
- CREATE INDEX idx_sale_seller_sold_at ON sale (seller_id, sold_at);
- CREATE INDEX idx_purchase_supplier_purchased_at ON purchase (supplier_id, purchased_at);
- CREATE INDEX idx_saleitem_drug ON sale_item (drug_id);
- CREATE INDEX idx_purchaseitem_drug ON purchase_item (drug_id);
- CREATE INDEX idx_drug_retail_price ON drug (retail_price);

## Notes / Next suggestions
- Consider adding `ON DELETE CASCADE` for other child relationships if business requires automatic cleanup; otherwise keep referential integrity strict.
- Consider adding unique constraints for fields that must be globally unique (e.g., drug.code is already unique).
- Consider normalizing warehouse into its own table if further fields are needed (location, manager, etc.).

## Next steps I can take
1. Implement `03_indexes.sql` with the recommended indexes and run explain on representative queries. 
2. Add Flyway-based migrations to manage these schema changes reliably across environments. (Completed: `src/main/resources/db/migration/V1__init_schema.sql` and `V2__add_indexes.sql` added.)
3. Create richer seed data in `04_init_data.sql` to cover edge cases (expired batches, low stock, returns). (Completed: added representative seed records in `database/04_init_data.sql`.)

If you'd like, I can proceed to implement the next tasks (add CI workflow and verify migrations in CI) — I've added a basic GitHub Actions workflow at `.github/workflows/ci.yml` to run `./mvnw test` on push/PR.  
