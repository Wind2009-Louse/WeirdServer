import pymysql
import os
import re

dump_name = "dump_log.sql"
dest_name = "dump_log_mysql.sql"

with open(dump_name, "r", encoding="utf-8") as rf:
    raw_text = rf.read()

raw_text = raw_text.replace("PRAGMA foreign_keys=OFF;\n", "")
raw_text = raw_text.replace("ANALYZE sqlite_schema;\n", "")
raw_text = raw_text.replace("BEGIN TRANSACTION", "START TRANSACTION")
create_format = re.compile(r"CREATE TABLE[\s\S\r\n]+?\);\n", re.S)
raw_text = re.sub(create_format, "", raw_text)
for rm_key in ["sqlite_sequence", "CREATE INDEX","sqlite_stat", "VALUES\(1,'admin'"]:
    rm_format = re.compile("^[^\n]*%s[^\n]*\n"%rm_key, re.M)
    raw_text = re.sub(rm_format, "", raw_text)
raw_text = raw_text.replace("INSERT INTO package_card", "INSERT INTO package_card(`card_pk`,`card_name`,`package_id`,`rare`,`db_created_time`,`need_coin`)")
raw_text = raw_text.replace("INSERT INTO package_info", "INSERT INTO package_info(`package_id`,`package_name`,`db_created_time`,`order_num`,`detail`)")
raw_text = raw_text.replace("INSERT INTO roll_detail", "INSERT INTO roll_detail(`roll_id`,`card_pk`,`is_dust`,`db_created_time`,`card_name`,`rare`)")
raw_text = raw_text.replace("INSERT INTO roll_list", "INSERT INTO roll_list(`roll_id`,`roll_user_id`,`roll_package_id`,`is_disabled`,`db_created_time`)")
raw_text = raw_text.replace("INSERT INTO user_card_list", "INSERT INTO user_card_list(`user_id`,`card_pk`,`count`,`db_created_time`)")
raw_text = raw_text.replace("INSERT INTO collection", "INSERT INTO collection(`collection_id`,`user_id`,`card_pk`,`db_created_time`)")
raw_text = raw_text.replace("INSERT INTO deck_list", "INSERT INTO deck_list(`deck_id`,`user_id`,`deck_name`,`last_modify_time`,`db_created_time`,`share`)")
raw_text = raw_text.replace("INSERT INTO deck_detail", "INSERT INTO deck_detail(`detail_id`,`deck_id`,`code`,`count`,`type`,`db_created_time`)")
raw_text = raw_text.replace("INSERT INTO roulette_history", "INSERT INTO roulette_history(`history_id`,`user_name`,`detail`,`db_created_time`)")
raw_text = raw_text.replace("INSERT INTO roulette_config", "INSERT INTO roulette_config(`config_id`,`detail`,`rate`,`color`,`db_created_time`)")
raw_text = raw_text.replace("INSERT INTO card_history", "INSERT INTO card_history(`id`,`package_id`,`card_pk`,`old_name`,`new_name`,`rare`,`db_created_time`)")
raw_text = raw_text.replace("INSERT INTO forbidden", "INSERT INTO forbidden(`id`,`code`,`name`,`count`,`db_created_time`)")
raw_text = raw_text.replace("INSERT INTO user_data", "INSERT INTO user_data(`user_id`,`user_name`,`password`,`is_admin`,`nonaward_count`,`dust_count`,`duel_point`,`coin`,`daily_win`,`daily_lost`,`daily_award`,`weekly_dust_change_n`,`weekly_dust_change_r`,`weekly_dust_change_alter`,`roulette`,`roll_count`,`qq`,`disabled`,`double_rare_count`,`db_created_time`)")
raw_text = raw_text.replace("INSERT INTO record", "INSERT INTO record(`record_id`,`operator`,`detail`,`db_created_time`)")

with open(dest_name, "w", encoding="utf-8") as wf:
    wf.write(raw_text)
