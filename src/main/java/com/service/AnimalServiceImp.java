package com.service;

import com.entity.Animal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.repository.AnimalRepository;
import java.util.List;
import java.util.Optional;

@Service
public class AnimalServiceImp implements AnimalService
{
		@Autowired
		private AnimalRepository animalRepository;

		private Optional<Animal> getAnimalByNickname(String nickname)
		{
				return animalRepository.findAnimalByNickname(nickname);
		}

		@Override public List<Animal> getAllAnimals()
		{
				return animalRepository.findAll();
		}

		@Override public List<Animal> getAllByUser(String userName)
		{
				return animalRepository.findAllByUsername(userName);
		}

		@Override public Optional<Animal> getAnimalById(long animalId)
		{
				return animalRepository.findById(animalId);
		}

		@Override public boolean addAnimal(Animal animal)
		{
				Optional<Animal> foundAnimal = getAnimalById(animal.getId());
				if(foundAnimal.isPresent())
						return false;
				foundAnimal = getAnimalByNickname(animal.getNickname());
				if(foundAnimal.isPresent())
						return false;
				animalRepository.save(animal);
				return true;
		}

		@Override public boolean updateAnimal(Animal animal)
		{
				Optional<Animal> foundAnimal = getAnimalById(animal.getId());
				if(foundAnimal.isEmpty())
						return false;
				if(animal.getNickname() == foundAnimal.get().getNickname())
						return false;
				animalRepository.delete(foundAnimal.get());
				animalRepository.save(animal);
				return true;
		}

		@Override public boolean deleteAnimal(long animalId)
		{
				Optional<Animal> foundAnimal = getAnimalById(animalId);
				if(foundAnimal.isPresent())
				{
						foundAnimal = getAnimalByNickname(foundAnimal.get().getNickname());
						if(foundAnimal.isPresent())
						{
								animalRepository.delete(foundAnimal.get());
								return true;
						}
				}
				return false;
		}
}
