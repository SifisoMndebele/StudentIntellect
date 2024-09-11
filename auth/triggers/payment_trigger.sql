create or replace function update_balance_on_payment ()
    returns trigger as $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM accounts a WHERE a.uid = NEW.uid AND a.verified = true)
    THEN
        RAISE EXCEPTION 'Account must be verified before requesting a payment';
    ELSE
        UPDATE accounts
        SET balance = balance - NEW.amount
        WHERE uid = NEW.uid; 
        RETURN NEW;
    END IF;
END;
$$ language plpgsql;

create or replace trigger handle_payment_trigger
after insert on payments for each row
execute function update_balance_on_payment ();