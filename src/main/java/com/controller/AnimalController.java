package com.controller;

import com.entity.Animal;
import com.entity.User;
import com.service.AnimalService;
import com.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class AnimalController
{
		private UserService userService;

		private AnimalService animalService;

		@Autowired
		public AnimalController(UserService userService, AnimalService animalService)
		{
				this.userService = userService;
				this.animalService = animalService;
		}


		@GetMapping(value = "/users/animalList/{username}")
		@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
		public ResponseEntity<?> read(@PathVariable(name = "username") String username)
		{
				List<Animal> animals = animalService.getAllByUser(username);
				if(animals != null)
						if(!animals.isEmpty())
								return new ResponseEntity<>(animals, HttpStatus.OK);
				return new ResponseEntity<>("Animals not found", HttpStatus.NOT_FOUND);
		}


		@GetMapping(value = "/users/animals/{id}")
		@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
		public ResponseEntity<?> read(@PathVariable(name = "id") long id)
		{
				Optional<Animal> animal = animalService.getAnimalById(id);
				return animal.isPresent()
					? new ResponseEntity<>(animal.get(), HttpStatus.OK)
					: new ResponseEntity<>("Animal not found", HttpStatus.NOT_FOUND);
		}


		@PostMapping(value = "users/animals/{username}")
		@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
		public ResponseEntity<?> save(
			@PathVariable(name = "username") String username, @RequestBody List<Animal> animals)
		{
				Optional<User> user = userService.getUserByUsername(username);
				if(user.isEmpty())
						return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
				if(animals.isEmpty())
						return new ResponseEntity<>("Not a single animal was added to the request", HttpStatus.NO_CONTENT);
				List<Animal> addedAnimals = new ArrayList<>();
				animals.stream().forEach((animal) -> {
						animal.setUsername(username);
						boolean isAdded = animalService.addAnimal(animal);
						if(isAdded)
								addedAnimals.add(animal);
				});
				return new ResponseEntity<>(addedAnimals, HttpStatus.CREATED);
		}


		@DeleteMapping(value = "users/animals/{username}")
		@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
		public ResponseEntity<?> delete(@PathVariable(name = "username") String username, @RequestBody List<Animal> animals)
		{
				Optional<User> user = userService.getUserByUsername(username);
				if(user.isEmpty())
						return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
				if(animals.isEmpty())
						return new ResponseEntity<>("Not a single animal was added to the request by delete", HttpStatus.NOT_FOUND);
				List<Animal> deletedAnimals = new ArrayList<>();
				animals.stream().forEach((animal) -> {
						if(animal.getUsername().equals(username))
						{
								boolean isDeleted;
								isDeleted = animalService.deleteAnimal(animal.getId());
								if(isDeleted)
										deletedAnimals.add(animal);
						}
				});
				return deletedAnimals.isEmpty()? new ResponseEntity<>("Not a single animal has been deleted", HttpStatus.NOT_FOUND)
					: new ResponseEntity<>(deletedAnimals, HttpStatus.OK);
		}

		@PutMapping(value = "users/animals/{username}")
		@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
		public ResponseEntity<?> update(@PathVariable(name = "username") String username, @RequestBody List<Animal> animals)
		{
				Optional<User> user = userService.getUserByUsername(username);
				if(user.isEmpty())
						return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
				if(animals.isEmpty())
						return new ResponseEntity<>("Not a single animal has been was added to the request by update", HttpStatus.NO_CONTENT);
				List<Animal> updatedAnimals = new ArrayList<>();
				animals.stream().forEach((animal) -> {
						if(animal.getUsername().equals(username))
						{
								boolean isUpdated;
								isUpdated = animalService.updateAnimal(animal);
								if(isUpdated)
										updatedAnimals.add(animal);
						}
				});
				return updatedAnimals.isEmpty()? new ResponseEntity<String>("Not a single animal has been updated", HttpStatus.NOT_MODIFIED)
					: new ResponseEntity<>(updatedAnimals, HttpStatus.OK);
		}
}

