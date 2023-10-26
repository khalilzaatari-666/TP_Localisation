package ma.zaatari.localisation.position;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PositionService {

    @Autowired
    private PositionRepository positionRepository;

    // getters

    public List<Position> findAll() {
        return positionRepository.findAll();
    }

    public Position findById(Integer id) {
        return positionRepository.findById(id).get();
    }

    // Posters

    public void createPosition(Position pos) {
        positionRepository.save(pos);
    }

    // PUT methods

    public void update(Integer id,
            Position new_pos) {
        Position pos = positionRepository.findById(id).get();
        pos.setLatitude(new_pos.getLatitude());
        pos.setDatetime(new_pos.getDatetime());
        pos.setLongitude(new_pos.getLongitude());
        pos.setImei(new_pos.getImei());
        positionRepository.save(pos);
    }

    // DELETE methods

    public void delete(Integer id) {
        positionRepository.delete(positionRepository.findById(id).get());
    }
}
