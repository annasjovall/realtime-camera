/* A simple server in the internet domain using TCP
   The port number is passed as an argument */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include "camera.h"

int main(int argc, char *argv[])
{
  struct sockaddr_in servaddr;
  int listen_fd = socket(AF_INET, SOCK_STREAM, 0);
  bzero( &servaddr, sizeof(servaddr));
  servaddr.sin_family = AF_INET;
  servaddr.sin_addr.s_addr = htons(INADDR_ANY);
  servaddr.sin_port = htons(22000);
  bind(listen_fd, (struct sockaddr *) &servaddr, sizeof(servaddr));
  while (1) {
    listen(listen_fd, 10);
    int comm_fd = accept(listen_fd, (struct sockaddr*) NULL, NULL);
    camera* cam = camera_open();
    frame* camera_frame = camera_get_frame(cam);
    byte* camera_byte = get_frame_bytes(camera_frame);
    size_t frame_size = get_frame_size(camera_frame);
    size_t frame_height = get_frame_height(camera_frame);
    size_t frame_width = get_frame_width(camera_frame);
    int* frame_size_int = (int*) frame_size;
    printf("Size: %zu\n", frame_size);
    printf("Width: %zu\n", frame_width);
    printf("Height: %zu\n", frame_height);
    printf("Height: %p\n", frame_size_int);
    write(comm_fd, &frame_size_int, 10);
    write(comm_fd, camera_byte, frame_size);
  }

}
