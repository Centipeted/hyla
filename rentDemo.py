#!/usr/bin/env python3
import sys
import time
import requests

BASE_URL = "http://127.0.0.1:8089"
API_KEY  = "demo-api-key"
DOMAIN   = "bh"
LANGUAGE = "en"

PHONE    = "+361000000001"
BIKE     = "860001"

# ---------------------------------------------------------------------------
# Simple helper
# ---------------------------------------------------------------------------
def post(endpoint: str, payload: dict):
    url = f"{BASE_URL}{endpoint}"
    print(f"\n⇢ POST {url}")
    r = requests.post(url, json=payload, timeout=5)
    print(f"⇠ {r.status_code}")
    try:
        data = r.json()
        print(data)
    except Exception:
        data = None
        print(r.text)
    if r.status_code >= 400:
        sys.exit("✕ Request failed, aborting demo.")
    return data


# ---------------------------------------------------------------------------
# 1. REGISTER
# ---------------------------------------------------------------------------
register_req = {
    "apiKey":      API_KEY,
    "domain":      DOMAIN,
    "language":    LANGUAGE,
    "phone_number": PHONE
}
reg_res = post("/register.json", register_req)
time.sleep(2)

login_key = reg_res["user"]["loginKey"]
pin       = reg_res["user"]["pin"] 

# ---------------------------------------------------------------------------
# 2. LOGIN  (with loginKey)
# ---------------------------------------------------------------------------
login_req = {
    "apiKey":   API_KEY,
    "domain":   DOMAIN,
    "language": LANGUAGE,
    "loginKey": login_key,
    "mobile":   None,
    "pin":      None
}
post("/login.json", login_req)
time.sleep(2)

# ---------------------------------------------------------------------------
# 3. RENT BIKE
# ---------------------------------------------------------------------------
rent_req = {
    "apiKey":   API_KEY,
    "domain":   DOMAIN,
    "language": LANGUAGE,
    "loginKey": login_key,
    "bike":     BIKE
}
rent1 = post("/rent.json", rent_req)
rental_id_1 = rent1["rental"]["id"]
time.sleep(5)

# ---------------------------------------------------------------------------
# 4. PUT RENTAL ON BREAK
# ---------------------------------------------------------------------------
break_req = {
    "apiKey":   API_KEY,
    "domain":   DOMAIN,
    "language": LANGUAGE,
    "loginKey": login_key,
    "rental":   rental_id_1
}
post("/rentalBreak.json", break_req)
time.sleep(1)

# ---------------------------------------------------------------------------
# 5. BIKE LOCKS  (still on break)
# ---------------------------------------------------------------------------
lock_req = {
    "apiKey": API_KEY,
    "bike":   BIKE,
    "locked": True,
    "lat":    47.541624772579,
    "lng":    19.040489061606
}
post("/bikeState.json", lock_req)
time.sleep(3)

# ---------------------------------------------------------------------------
# 6. RENT BIKE AGAIN
# ---------------------------------------------------------------------------
rent2_req = {
    "apiKey":   API_KEY,
    "domain":   DOMAIN,
    "language": LANGUAGE,
    "loginKey": login_key,
    "bike":     BIKE
}
rent2 = post("/rent.json", rent2_req)
rental_id_2 = rent2["rental"]["id"]
time.sleep(5)

# ---------------------------------------------------------------------------
# 7. BIKE LOCKS  (ride finished)
# ---------------------------------------------------------------------------
finish_req = {
    "apiKey": API_KEY,
    "bike":   BIKE,
    "locked": True,
    "lat":    47.541600,
    "lng":    19.040400
}
post("/bikeState.json", finish_req)

print("\nDemo complete.")

