package uj.wmii.pwj.map2d;

import java.util.Map;
import java.util.HashMap;
import java.util.function.Function;
import java.util.Collections;

public class Map_2d_implementation<R, C, V> implements Map2D<R, C, V> {
    private final Map<R, Map<C, V>> map = new HashMap<>();

    @Override
    public V put(R rowKey, C columnKey, V value) {
        if (rowKey == null || columnKey == null) {
            throw new NullPointerException("Row key and column key cannot be null.");
        }

        if (!map.containsKey(rowKey)) {
            map.put(rowKey, new HashMap<>());
        }

        return map.get(rowKey).put(columnKey, value);
    }

    @Override
    public V get(R rowKey, C columnKey) {

        if (rowKey == null || columnKey == null) {
            throw new NullPointerException("rowKey and columnKey cannot be null");
        }

        Map<C, V> row = map.get(rowKey);
        if (row == null) {
            return null;
        } else {
            V value = map.get(rowKey).get(columnKey);
            return value;
        }

    }

    @Override
    public V getOrDefault(R rowKey, C columnKey, V defaultValue) {
        if (rowKey == null || columnKey == null) {
            throw new NullPointerException("rowKey and columnKey cannot be null");
        }

        Map<C, V> row = map.get(rowKey);
        if (row == null) {
            return defaultValue;
        } else {
            V value = map.get(rowKey).get(columnKey);
            if (value == null) {
                return defaultValue;
            } else {
                return value;
            }
        }
    }

    @Override
    public V remove(R rowKey, C columnKey) {
        if (rowKey == null || columnKey == null) {
            throw new NullPointerException("rowKey and columnKey cannot be null");
        }

        Map<C, V> row = map.get(rowKey);
        if (row == null) {
            return null;
        } else {
            V value = map.get(rowKey).remove(columnKey);

            if (value == null) {
                return null;
            } else {
                if (map.get(rowKey).size() == 0) {
                    map.remove(rowKey);
                }
                return value;
            }
        }
    }


    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean nonEmpty() {
        return !map.isEmpty();
    }

    @Override
    public int size() {
        int size = 0;
        for (Map<C, V> row : map.values()) {
            size += row.size();
        }
        return size;
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Map<C, V> rowView(R rowKey) {
        if (rowKey == null) {
            throw new NullPointerException("rowKey cannot be null");
        }

        Map<C, V> row = map.get(rowKey);
        if (row == null) {
            return Collections.emptyMap();
        } else {
            if (row.size() == 0) {
                return Collections.emptyMap();
            } else {
                Map<C, V> immutableRow = Collections.unmodifiableMap(row);
                return immutableRow;
            }

        }


    }

    @Override
    public Map<R, V> columnView(C columnKey) {
        if (columnKey == null) {
            throw new NullPointerException("rowKey and columnKey cannot be null");
        }

        Map<R, V> column = new HashMap<>();
        for (Map.Entry<R, Map<C, V>> entry : map.entrySet()) {
            R rowKey = entry.getKey();
            Map<C, V> row = entry.getValue();

            if (row.containsKey(columnKey)) {
                column.put(rowKey, row.get(columnKey));
            }
        }

        if (column == null) {
            return Collections.emptyMap();
        } else {
            if (column.size() == 0) {
                return Collections.emptyMap();
            } else {
                Map<R, V> immutableColumn = Collections.unmodifiableMap(column);
                return immutableColumn;
            }
        }
    }

    @Override
    public boolean containsValue(V value) {
        for (Map<C, V> row : map.values()) {
            if (row.containsValue(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsKey(R rowKey, C columnKey) {

        if (rowKey == null || columnKey == null) {
            throw new NullPointerException("rowKey and columnKey cannot be null");
        }

        Map<C, V> row = map.get(rowKey);
        if (row != null) {
            V value = row.get(columnKey);
            return value != null;
        }

        return false;
    }

    @Override
    public boolean containsRow(R rowKey) {
        if (rowKey == null) {
            throw new NullPointerException("rowKey and columnKey cannot be null");
        }

        if (map.containsKey(rowKey)) {
            return map.get(rowKey).size() != 0;
        }

        return false;
    }

    @Override
    public boolean containsColumn(C columnKey) {
        if (columnKey == null) {
            throw new NullPointerException("rowKey and columnKey cannot be null");
        }

        for (Map<C, V> row : map.values()) {
            if (row.containsKey(columnKey)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Map<R, Map<C, V>> rowMapView() {
        Map<R, Map<C, V>> immutableRowMap = new HashMap<>();
        for (Map.Entry<R, Map<C, V>> entry : map.entrySet()) {
            R rowKey = entry.getKey();
            Map<C, V> row = entry.getValue();
            immutableRowMap.put(rowKey, Collections.unmodifiableMap(new HashMap<>(row)));
        }
        return Collections.unmodifiableMap(immutableRowMap);
    }


    @Override
    public Map<C, Map<R, V>> columnMapView() {
        Map<C, Map<R, V>> ColumnMap = new HashMap<>();
        for (Map.Entry<R, Map<C, V>> entry : map.entrySet()) {
            R rowKey = entry.getKey();
            Map<C, V> row = entry.getValue();
            for (Map.Entry<C, V> columnEntry : row.entrySet()) {
                C columnKey = columnEntry.getKey();
                V value = columnEntry.getValue();
                Map<R, V> column_temp = ColumnMap.get(columnKey);
                if (column_temp == null) {
                    column_temp = new HashMap<>();
                    ColumnMap.put(columnKey, column_temp);
                }
                column_temp.put(rowKey, value);
            }
        }

        Map<C, Map<R, V>> immutableColumnMap = new HashMap<>();
        for (Map.Entry<C, Map<R, V>> entry : ColumnMap.entrySet()) {
            immutableColumnMap.put(entry.getKey(), Collections.unmodifiableMap(entry.getValue()));
        }

        return Collections.unmodifiableMap(immutableColumnMap);
    }

    @Override
    public Map2D<R, C, V> fillMapFromRow(Map<? super C, ? super V> target, R rowKey) {

        if (rowKey == null) {
            throw new NullPointerException("rowKey cannot be null");
        }

        if (map.containsKey(rowKey)) {
            Map<C, V> row = map.get(rowKey);
            if (row != null) {
                for (Map.Entry<C, V> columnEntry : row.entrySet()) {
                    target.put(columnEntry.getKey(), columnEntry.getValue());
                }
            }
        }

        return this;
    }

    @Override
    public Map2D<R, C, V> fillMapFromColumn(Map<? super R, ? super V> target, C columnKey) {
        if (columnKey == null) {
            throw new NullPointerException("columnKey cannot be null");
        }

        for (Map.Entry<R, Map<C, V>> entry : map.entrySet()) {
            R rowKey = entry.getKey();
            Map<C, V> row = entry.getValue();
            if (row != null) {
                target.put(rowKey, row.get(columnKey));
            }
        }
        return this;
    }

    @Override
    public Map2D<R, C, V> putAll(Map2D<? extends R, ? extends C, ? extends V> source) {

        if (source == null) {
            throw new NullPointerException("source cannot be null");
        }


        for (Map.Entry<? extends R, ? extends Map<? extends C, ? extends V>> rowEntry : source.rowMapView().entrySet()) {
            R rowKey = rowEntry.getKey();
            Map<? extends C, ? extends V> row = rowEntry.getValue();

            for (Map.Entry<? extends C, ? extends V> columnEntry : row.entrySet()) {
                C columnKey = columnEntry.getKey();
                V value = columnEntry.getValue();

                this.put(rowKey, columnKey, value);
            }
        }
        return this;
    }

    @Override
    public Map2D<R, C, V> putAllToRow(Map<? extends C, ? extends V> source, R rowKey) {
        if (rowKey == null) {
            throw new NullPointerException("rowKey cannot be null");
        }

        Map<C, V> row = map.get(rowKey);
        if (row != null) {
            for (Map.Entry<? extends C, ? extends V> columnEntry : source.entrySet()) {
                C columnKey = columnEntry.getKey();
                V value = columnEntry.getValue();
                this.put(rowKey, columnKey, value);
            }
        } else {
            map.put(rowKey, new HashMap<>(source));
        }

        return this;
    }

    @Override
    public Map2D<R, C, V> putAllToColumn(Map<? extends R, ? extends V> source, C columnKey) {
        if (columnKey == null) {
            throw new NullPointerException("columnKey cannot be null");
        }

        for (Map.Entry<? extends R, ? extends V> RowEntry : source.entrySet()) {
            R RowKey = RowEntry.getKey();
            V value = RowEntry.getValue();
            this.put(RowKey, columnKey, value);
        }

        return this;
    }

    @Override
    public <R2, C2, V2> Map2D<R2, C2, V2> copyWithConversion(Function<? super R, ? extends R2> rowFunction, Function<? super C, ? extends C2> columnFunction, Function<? super V, ? extends V2> valueFunction) {
        Map2D<R2, C2, V2> newMap = new Map_2d_implementation<>();
        for (Map.Entry<R, Map<C, V>> rowEntry : map.entrySet()) {
            R rowKey = rowEntry.getKey();
            R2 rowKeyNew = rowFunction.apply(rowKey);
            Map<C, V> row = rowEntry.getValue();
            for (Map.Entry<C, V> columnEntry : row.entrySet()) {
                C columnKey = columnEntry.getKey();
                C2 columnKeyNew = columnFunction.apply(columnKey);
                V value = columnEntry.getValue();
                V2 valueNew = valueFunction.apply(value);

                newMap.put(rowKeyNew, columnKeyNew, valueNew);
            }
        }

        return newMap;
    }
}