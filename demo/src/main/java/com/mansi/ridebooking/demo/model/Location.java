@Entity
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String locationName;

    private double latitude;

    private double longitude;
}
