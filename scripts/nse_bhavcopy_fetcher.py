import os
import io
import zipfile
import requests
import pandas as pd
from datetime import datetime, timedelta
import psycopg2

def run_pipeline():
    db_url = os.environ.get("DATABASE_URL")
    if not db_url:
        raise ValueError("DATABASE_URL environment variable nahi mila!")

    # Aaj ya previous working day ki date select karein
    target_date = datetime.now()
    if target_date.weekday() == 5: # Saturday
        target_date -= timedelta(days=1)
    elif target_date.weekday() == 6: # Sunday
        target_date -= timedelta(days=2)

    year = target_date.strftime("%Y")
    month = target_date.strftime("%b").upper()
    day = target_date.strftime("%d")
    date_str = target_date.strftime("%Y-%m-%d")

    url = f"https://archives.nseindia.com/content/historical/EQUITIES/{year}/{month}/cm{day}{month}{year}bhav.csv.zip"
    headers = {
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
        "Referer": "https://www.nseindia.com"
    }

    session = requests.Session()
    session.get("https://www.nseindia.com", headers=headers, timeout=10)
    res = session.get(url, headers=headers, timeout=15)

    if res.status_code != 200:
        print(f"Data download nahi hua (Holiday ya Market close): Status {res.status_code}")
        return

    with zipfile.ZipFile(io.BytesIO(res.content)) as z:
        with z.open(z.namelist()[0]) as f:
            df = pd.read_csv(f)

    # Sirf Equity (EQ) filter karein
    df = df[df["SERIES"] == "EQ"][["SYMBOL", "CLOSE", "TOTTRDQTY"]]
    
    conn = psycopg2.connect(db_url)
    cursor = conn.cursor()

    query = """
        INSERT INTO stock_eod_prices (ticker, close_price, volume, trade_date)
        VALUES (%s, %s, %s, %s)
        ON CONFLICT (ticker, trade_date) 
        DO UPDATE SET close_price = EXCLUDED.close_price, volume = EXCLUDED.volume;
    """

    records = [
        (row["SYMBOL"], float(row["CLOSE"]), int(row["TOTTRDQTY"]), date_str)
        for _, row in df.iterrows()
    ]

    cursor.executemany(query, records)
    conn.commit()
    cursor.close()
    conn.close()
    print(f"Successfully uploaded {len(records)} stocks for {date_str}")

if __name__ == "__main__":
    run_pipeline()
