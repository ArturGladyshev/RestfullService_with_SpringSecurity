package com.service;

import com.entity.Animal;
import com.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImp implements UserService
{

		@Autowired
		private AnimalService animalService;

		@Autowired
		private UserRepository userRepository;

		@Override
		public boolean addUser(User user)
		{
				User foundUser = getUserById(user.getId());
				if(foundUser != null)
						return false;
				if(user.getAnimals() != null)
						user.getAnimals().forEach(animal -> {
								animal.setUsername(user.getUsername());
								animalService.addAnimal(animal);
						});
				userRepository.save(user);
				return true;
		}

		@Override
		public List<User> getAllUsers()
		{
				return userRepository.findAll();
		}

		@Override
		public User getUserById(long userId)
		{
				return userRepository.findById(userId);
		}


		@Override
		public Optional<User> getUserByUsername(String username)
		{
				return userRepository.findByUsername(username);
		}

		@Override
		public boolean updateUser(User user)
		{
				User foundUser = getUserById(user.getId());
				if(foundUser != null)
				{
						if(user.getAnimals() == null && foundUser.getAnimals() != null)
						{
								foundUser.getAnimals().stream().forEach(animal -> animalService.deleteAnimal(animal.getId()));
						}
						if(user.getAnimals() != null)
						{
								user.getAnimals().stream().forEach(animal -> animal.setUsername(user.getUsername()));
								if(foundUser.getAnimals() != null)
								{
										List<Animal> animals = user.getAnimals().stream().filter(animal -> foundUser.getAnimals().
											contains(animal) || !foundUser.getAnimals().contains(animal)).collect(Collectors.toList());
										user.setAnimals(animals);
										foundUser.getAnimals().stream().forEach(animal -> {
												if(!animals.contains(animal))
														animalService.deleteAnimal(animal.getId());
										});
								}
								user.getAnimals().stream().forEach(animal -> animalService.addAnimal(animal));
						}
						userRepository.save(user);
						return true;
				}
				return false;
		}

		@Override
		public void deleteUser(String userName)
		{
				Optional<User> optionalUser = this.getUserByUsername(userName);
				if(optionalUser.isPresent())
				{
						userRepository.delete(optionalUser.get());
						if(optionalUser.get().getAnimals() != null)
								optionalUser.get().getAnimals().forEach(animal -> animalService.deleteAnimal(animal.getId()));
				}
		}
}
