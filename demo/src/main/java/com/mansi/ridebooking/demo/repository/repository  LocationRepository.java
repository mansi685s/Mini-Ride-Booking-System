public interface LocationRepository extends JpaRepository<Location,Integer>{

    Location findByLocationName(String locationName);

}
