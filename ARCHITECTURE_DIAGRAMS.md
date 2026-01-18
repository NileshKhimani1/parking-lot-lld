# Architecture & Design Diagrams

## 1. System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                          PARKING LOT SYSTEM                         │
│                                                                      │
│  ┌────────────────┐                                                 │
│  │  Entry Panel   │ ─────────┐                                      │
│  └────────────────┘          │                                      │
│                               │                                      │
│  ┌────────────────┐          │                                      │
│  │  Exit Panel    │ ─────────┼──────────┐                           │
│  └────────────────┘          │          │                           │
│                               ▼          ▼                           │
│                        ┌─────────────────────────┐                  │
│                        │   ParkingFloor (F1)     │                  │
│                        │                         │                  │
│                        │  Vehicle Managers:      │                  │
│                        │  ┌───────────────────┐  │                  │
│                        │  │ TwoWheeler Mgr    │  │                  │
│                        │  │ (MOTORCYCLE)      │  │                  │
│                        │  │                   │  │                  │
│                        │  │ Spots:            │  │                  │
│                        │  │ • Motorcycle      │  │                  │
│                        │  │ • Motorcycle      │  │                  │
│                        │  └───────────────────┘  │                  │
│                        │                         │                  │
│                        │  ┌───────────────────┐  │                  │
│                        │  │ FourWheeler Mgr   │  │                  │
│                        │  │ (CAR/ELECTRIC)    │  │                  │
│                        │  │                   │  │                  │
│                        │  │ Spots:            │  │                  │
│                        │  │ • Compact         │  │                  │
│                        │  │ • Electric        │  │                  │
│                        │  │ • Compact         │  │                  │
│                        │  └───────────────────┘  │                  │
│                        │                         │                  │
│                        │  ┌───────────────────┐  │                  │
│                        │  │ Large Vehicle Mgr │  │                  │
│                        │  │ (TRUCK/VAN)       │  │                  │
│                        │  │                   │  │                  │
│                        │  │ Spots:            │  │                  │
│                        │  │ • Large           │  │                  │
│                        │  └───────────────────┘  │                  │
│                        └─────────────────────────┘                  │
│                                                                      │
│                        ┌─────────────────────────┐                  │
│                        │   ParkingFloor (F2)     │                  │
│                        │                         │                  │
│                        │  [Similar Structure]    │                  │
│                        └─────────────────────────┘                  │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 2. Concurrent Access Pattern

### Without Vehicle Managers (Traditional Approach)

```
Thread 1 (Park Car)      Thread 2 (Park Bike)      Thread 3 (Park Truck)
       │                       │                            │
       ▼                       │                            │
   Acquire Floor Lock          │                            │
       │                       │                            │
       ├─── BLOCKED ────────┤ BLOCKED ────────┤ BLOCKED ────┤
       │                   │                  │              │
   Park Car (5ms)         │                   │              │
       │                   │                  │              │
   Release Floor Lock      │                  │              │
       │                   │                  │              │
       ├───────────────────►                  │              │
                      Acquire Floor Lock      │              │
                           │                  │              │
                           ├──── BLOCKED ─────┤ BLOCKED ─────┤
                           │                 │              │
                      Park Bike (3ms)        │              │
                           │                 │              │
                      Release Floor Lock     │              │
                           │                 │              │
                           ├─────────────────►              │
                                        Acquire Floor Lock   │
                                             │              │
                                        Park Truck (4ms)     │
                                             │              │
                                        Release Floor Lock   │
                                             │              │
                                             ▼
Total Time: 5ms + 3ms + 4ms = 12ms (SEQUENTIAL)
```

### With Vehicle Managers (Our Approach)

```
Thread 1 (Park Car)      Thread 2 (Park Bike)      Thread 3 (Park Truck)
       │                       │                            │
       ▼                       ▼                            ▼
   Acquire Car Mgr Lock    Acquire Bike Mgr Lock   Acquire Truck Mgr Lock
       │                       │                            │
       ◄── PARALLEL EXECUTION POSSIBLE ──────────────────────►
       │                       │                            │
   Park Car (5ms) ║      Park Bike (3ms) ║         Park Truck (4ms)
       │          ║           │           ║                 │
   Release Lock   ║      Release Lock     ║            Release Lock
       │          ║           │           ║                 │
       ▼          ▼           ▼           ▼                 ▼
       ├──────────┼───────────┤
       
Total Time: MAX(5ms, 3ms, 4ms) = 5ms (CONCURRENT)
Speedup: 12ms / 5ms = 2.4x faster!
```

---

## 3. Lock Management

```
┌─────────────────────────────────────────────────────┐
│          ParkingFloor Instance                      │
├─────────────────────────────────────────────────────┤
│                                                     │
│  ┌──────────────────────────────────────────────┐  │
│  │ Manager 1: TwoWheelerManager                 │  │
│  │ ┌────────────────────────────────────────┐   │  │
│  │ │ LOCK 1: Object lock                    │   │  │
│  │ │ [Motorcycle Spot 1] [Motorcycle Spot 2]│   │  │
│  │ └────────────────────────────────────────┘   │  │
│  └──────────────────────────────────────────────┘  │
│              ▲ Independent                         │
│              │                                     │
│  ┌──────────────────────────────────────────────┐  │
│  │ Manager 2: FourWheelerManager                │  │
│  │ ┌────────────────────────────────────────┐   │  │
│  │ │ LOCK 2: Object lock                    │   │  │
│  │ │ [Compact Spot] [Electric Spot] [...]   │   │  │
│  │ └────────────────────────────────────────┘   │  │
│  └──────────────────────────────────────────────┘  │
│              ▲ Independent                         │
│              │                                     │
│  ┌──────────────────────────────────────────────┐  │
│  │ Manager 3: LargeVehicleManager               │  │
│  │ ┌────────────────────────────────────────┐   │  │
│  │ │ LOCK 3: Object lock                    │   │  │
│  │ │ [Large Spot 1] [Large Spot 2]          │   │  │
│  │ └────────────────────────────────────────┘   │  │
│  └──────────────────────────────────────────────┘  │
│              ▲ Independent                         │
│              │                                     │
└──────────────────────────────────────────────────┘

Thread 1 acquires LOCK 1 ─┐
                           ├─► Can execute SIMULTANEOUSLY
Thread 2 acquires LOCK 2 ─┘
```

---

## 4. Class Hierarchy

```
                    ┌──────────────────┐
                    │ VehicleManager   │
                    │  (abstract)      │
                    │                  │
                    │ + parkVehicle()  │
                    │ + releaseVehicle()│
                    │ + hasAvailableSpot│
                    │ + addSpot()       │
                    └──────────────────┘
                           ▲
                ┌──────────┼──────────┐
                │          │          │
        ┌───────┴────────┐ │  ┌─────┴──────────┐
        │ TwoWheeler     │ │  │ FourWheeler    │
        │ Manager        │ │  │ Manager        │
        ├────────────────┤ │  ├────────────────┤
        │ Vehicle Type:  │ │  │ Vehicle Type:  │
        │  MOTORCYCLE    │ │  │  CAR/ELECTRIC  │
        │                │ │  │                │
        │ Spot Type:     │ │  │ Spot Type:     │
        │  MOTORCYCLE    │ │  │  COMPACT,      │
        │                │ │  │  ELECTRIC      │
        │ Lock Scope:    │ │  │                │
        │  2-wheeler     │ │  │ Lock Scope:    │
        │  spots only    │ │  │  4-wheeler     │
        │                │ │  │  spots only    │
        └────────────────┘ │  └────────────────┘
                           │
                        ┌──┴──────────────┐
                        │ LargeVehicle    │
                        │ Manager         │
                        ├─────────────────┤
                        │ Vehicle Type:   │
                        │  TRUCK/VAN      │
                        │                 │
                        │ Spot Type:      │
                        │  LARGE          │
                        │                 │
                        │ Lock Scope:     │
                        │  Large vehicle  │
                        │  spots only     │
                        └─────────────────┘
```

---

## 5. Spot Assignment Flow

```
Vehicle Arrives
      │
      ▼
┌──────────────────────────┐
│ ParkingFloor.parkVehicle │
│   (Vehicle vehicle)      │
└──────────┬───────────────┘
           │
           ▼
  ┌─────────────────────────────────────────┐
  │ Get Manager for Vehicle Type            │
  └──────────┬──────────────────────────────┘
             │
        ┌────┴──────┬─────────┬────────────────┐
        │            │         │                │
        ▼            ▼         ▼                ▼
    [CAR]     [MOTORCYCLE]  [TRUCK]        [ELECTRIC]
        │            │         │                │
    FourWheeler  TwoWheeler Large         FourWheeler
    Manager      Manager     Vehicle      Manager
        │            │        Manager       │
        │            │         │            │
        ├────────┬───┼─────┬───┴─────┬──────┘
        │        │   │     │         │
        │        │   │     │         │
        ▼        ▼   ▼     ▼         ▼
    ┌───────────────────────────────────────────┐
    │  Manager.parkVehicle(vehicle, ticketId)   │
    │                                           │
    │  synchronized(lock) {                     │
    │    1. Check available spot                │
    │    2. Occupy spot                         │
    │    3. Create ticket                       │
    │    4. Return ticket                       │
    │  }                                        │
    └──────────────┬──────────────────────────┘
                   │
                   ▼
            ┌─────────────────┐
            │ ParkingTicket   │
            │ + Vehicle       │
            │ + Spot          │
            │ + Entry Time    │
            └─────────────────┘
                   │
                   ▼
            Vehicle Parked ✓
```

---

## 6. Release Flow

```
Vehicle Exits
      │
      ▼
┌─────────────────────────────┐
│ ExitPanel.exit(ticket)      │
│                             │
│ 1. Verify Payment ✓         │
└──────────────┬──────────────┘
               │
               ▼
      ┌─────────────────────────────────────┐
      │ ParkingFloor.releaseVehicle(ticket) │
      └──────────┬────────────────────────┘
                 │
                 ▼
        ┌────────────────────────────────────┐
        │ Get Vehicle from Ticket            │
        │ Lookup appropriate Manager         │
        └──────────┬─────────────────────────┘
                   │
                   ▼
        ┌────────────────────────────────────┐
        │ Manager.releaseVehicle(ticket)     │
        │                                    │
        │ synchronized(lock) {               │
        │   ticket.getSpot().release()       │
        │ }                                  │
        └──────────┬────────────────────────┘
                   │
                   ▼
            Spot Available ✓
                   │
                   ▼
            Update Display
                   │
                   ▼
            Exit Successful ✓
```

---

## 7. State Transitions

```
ParkingSpot State Machine:

    ┌──────────────┐
    │ EMPTY (free) │
    └──────┬───────┘
           │ occupy()
           │
           ▼
    ┌──────────────┐
    │  OCCUPIED    │
    │  (in use)    │
    └──────┬───────┘
           │ release()
           │
           ▼
    ┌──────────────┐
    │ EMPTY (free) │
    └──────────────┘


ParkingTicket State Machine:

    ┌──────────────┐
    │   ACTIVE     │  (Vehicle parked, payment due)
    └──────┬───────┘
           │ pay()
           │
           ▼
    ┌──────────────┐
    │    PAID      │  (Payment completed)
    └──────┬───────┘
           │ exit()
           │
           ▼
    Vehicle Leaves
```

---

## 8. Scalability: Multiple Floors

```
┌────────────────────────────────────────────────────────────────┐
│                    ParkingLot                                  │
├────────────────────────────────────────────────────────────────┤
│                                                                 │
│   ┌──────────────────────────┐   ┌──────────────────────────┐  │
│   │   Floor 1                │   │   Floor 2                │  │
│   │                          │   │                          │  │
│   │  Managers with Locks:    │   │  Managers with Locks:    │  │
│   │  • TwoWheeler (Lock 1)   │   │  • TwoWheeler (Lock 4)   │  │
│   │  • FourWheeler (Lock 2)  │   │  • FourWheeler (Lock 5)  │  │
│   │  • LargeVehicle (Lock 3) │   │  • LargeVehicle (Lock 6) │  │
│   │                          │   │                          │  │
│   └──────────────────────────┘   └──────────────────────────┘  │
│                                                                 │
│   ┌──────────────────────────┐   ┌──────────────────────────┐  │
│   │   Floor 3                │   │   Floor N...             │  │
│   │                          │   │                          │  │
│   │  Managers with Locks:    │   │  Managers with Locks:    │  │
│   │  • TwoWheeler (Lock 7)   │   │  • TwoWheeler (Lock...)  │  │
│   │  • FourWheeler (Lock 8)  │   │  • FourWheeler (Lock...) │  │
│   │  • LargeVehicle (Lock 9) │   │  • LargeVehicle (Lock...)│  │
│   │                          │   │                          │  │
│   └──────────────────────────┘   └──────────────────────────┘  │
│                                                                 │
│  Total Independent Locks: 3 × NumberOfFloors                   │
│  For 10 floors: 30 independent locks!                          │
│                                                                 │
└────────────────────────────────────────────────────────────────┘
```

---

## 9. Concurrency Performance Graph

```
Operations Per Second vs Concurrent Threads

1000│                                        ▲ With Managers
    │                                      ╱│
    │                                   ╱   │
 800│                                ╱      │
    │                             ╱         │
    │                          ╱            │
 600│                       ╱               │
    │                    ╱                  │
    │                 ╱                     │
 400│              ╱                        │
    │           ╱                           │
    │        ╱                              │
 200│     ╱─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─  ← Without Managers
    │ ╱   (throughput plateaus due to lock contention)
    │
    └────────────────────────────────────────
      0   10   20   30   40   50  Threads

With Managers: Linear scaling with number of vehicle types
Without Managers: Plateaus due to single lock
```

---

## 10. Integration Points

```
External Systems
      │
      ├─────────────────┬──────────────┬────────────────┐
      │                 │              │                │
      ▼                 ▼              ▼                ▼
 ┌─────────┐     ┌────────────┐   ┌────────┐    ┌──────────────┐
 │Entry    │     │Info Portal │   │Exit    │    │Display Board │
 │Panel    │     │(Payment)   │   │Panel   │    │              │
 └────┬────┘     └──────┬─────┘   └───┬────┘    └──────────────┘
      │                 │             │
      │                 └─────┬───────┘
      │                       │
      ▼                       ▼
 ┌────────────────────────────────────┐
 │      ParkingFloor                  │
 │  (Vehicle Manager Coordinator)     │
 └────────┬────────────┬──────┬───────┘
          │            │      │
  ┌───────┴───┐ ┌─────┴────┐ ┌──────┴──────┐
  │           │ │          │ │             │
  ▼           ▼ ▼          ▼ ▼             ▼
[TwoWheel] [FourWheel] [LargeVehicle]
 Manager    Manager      Manager
```

---

This architecture ensures:
- ✅ High concurrency
- ✅ No bottlenecks
- ✅ Scalable design
- ✅ Type-safe operations
- ✅ Easy maintenance
