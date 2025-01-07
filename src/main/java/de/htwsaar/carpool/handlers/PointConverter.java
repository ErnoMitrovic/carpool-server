package de.htwsaar.carpool.handlers;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKBReader;
import org.locationtech.jts.io.WKBWriter;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;

import java.nio.charset.StandardCharsets;

@Converter(autoApply = true)
public class PointConverter implements AttributeConverter<Point, byte[]> {

    private static final GeometryFactory geometryFactory = new GeometryFactory();
    private static final WKBWriter wkbWriter = new WKBWriter();
    private static final WKBReader wkbReader = new WKBReader(geometryFactory);

    @Override
    public byte[] convertToDatabaseColumn(Point point) {
        return (point != null) ? wkbWriter.write(point) : null;  // Convert Point to WKB (Binary)
    }

    @Override
    public Point convertToEntityAttribute(byte[] dbData) {
        try {
            return (dbData != null) ? (Point) wkbReader.read(dbData) : null; // Convert WKB (Binary) to Point
        } catch (Exception e) {
            throw new RuntimeException("Error converting WKB to Point", e);
        }
    }
}
